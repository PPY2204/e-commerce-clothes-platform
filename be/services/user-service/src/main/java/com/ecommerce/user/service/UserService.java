package com.ecommerce.user.service;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.user.dto.*;
import com.ecommerce.user.model.User;
import com.ecommerce.user.model.VerificationCode;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.repository.VerificationCodeRepository;
import com.ecommerce.user.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

        private final UserRepository userRepository;
        private final VerificationCodeRepository verificationCodeRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtTokenProvider jwtTokenProvider;
        private final AuthenticationManager authenticationManager;
        private final org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate;

        public AuthResponse register(RegisterRequest request) {
                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new BusinessException("Email already exists");
                }

                User user = new User();
                user.setEmail(request.getEmail());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setFirstName(request.getFirstName());
                user.setLastName(request.getLastName());
                user.setPhone(request.getPhone());
                user.setRole(User.Role.USER);
                user.setActive(false); // Default to inactive until OTP verification

                user = userRepository.save(user);

                // Generate and save OTP
                String otp = String.format("%06d", new java.util.Random().nextInt(999999));
                VerificationCode verificationCode = VerificationCode.builder()
                                .code(otp)
                                .user(user)
                                .expiryDate(java.time.LocalDateTime.now().plusMinutes(15))
                                .build();

                verificationCodeRepository.save(verificationCode);

                log.info("Generated OTP for user {}: {}", user.getEmail(), otp);

                // Send OTP via Kafka to NotificationService
                com.ecommerce.common.event.UserRegisteredEvent event = com.ecommerce.common.event.UserRegisteredEvent
                                .builder()
                                .email(user.getEmail())
                                .firstName(user.getFirstName())
                                .otp(otp)
                                .build();

                kafkaTemplate.send("user-registration", event);
                log.info("Sent UserRegisteredEvent to Kafka for user: {}", user.getEmail());

                return new AuthResponse(null, user.getId(), user.getEmail(),
                                user.getFirstName(), user.getLastName(), user.getRole().name());
        }

        public void verifyOtp(String email, String code) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                if (user.getActive()) {
                        throw new BusinessException("User is already active");
                }

                VerificationCode verificationCode = verificationCodeRepository.findByCode(code)
                                .filter(vc -> vc.getUser().getId().equals(user.getId()))
                                .orElseThrow(() -> new BusinessException("Invalid verification code"));

                if (verificationCode.isExpired()) {
                        throw new BusinessException("Verification code has expired");
                }

                user.setActive(true);
                userRepository.save(user);
                verificationCodeRepository.delete(verificationCode);

                log.info("User {} activated successfully", email);
        }

        public AuthResponse login(LoginRequest request) {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                if (!Boolean.TRUE.equals(user.getActive())) {
                        throw new BusinessException("Please verify your account via OTP before logging in");
                }

                String token = jwtTokenProvider.generateToken(user.getEmail());

                return new AuthResponse(token, user.getId(), user.getEmail(),
                                user.getFirstName(), user.getLastName(), user.getRole().name());
        }

        public UserResponse getUserById(Long id) {
                User user = userRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
                return mapToResponse(user);
        }

        public UserResponse getUserByEmail(String email) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
                return mapToResponse(user);
        }

        public List<UserResponse> getAllUsers() {
                return userRepository.findAll().stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        public UserResponse updateUser(Long id, RegisterRequest request) {
                User user = userRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

                user.setFirstName(request.getFirstName());
                user.setLastName(request.getLastName());
                user.setPhone(request.getPhone());

                user = userRepository.save(user);
                return mapToResponse(user);
        }

        public void deleteUser(Long id) {
                User user = userRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
                userRepository.delete(user);
        }

        public void forgotPassword(String email) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                // Generate and save OTP for password reset
                String otp = String.format("%06d", new java.util.Random().nextInt(999999));
                verificationCodeRepository.deleteByUserId(user.getId()); // Clear old codes

                VerificationCode verificationCode = VerificationCode.builder()
                                .code(otp)
                                .user(user)
                                .expiryDate(java.time.LocalDateTime.now().plusMinutes(10))
                                .build();

                verificationCodeRepository.save(verificationCode);

                // Send Notification Event
                com.ecommerce.common.event.NotificationEvent event = com.ecommerce.common.event.NotificationEvent
                                .builder()
                                .email(user.getEmail())
                                .firstName(user.getFirstName())
                                .content(otp)
                                .type("FORGOT_PASSWORD")
                                .build();

                kafkaTemplate.send("notification-events", event);
                log.info("Sent FORGOT_PASSWORD event for user: {}", email);
        }

        public void resetPassword(String email, String code, String newPassword) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                VerificationCode verificationCode = verificationCodeRepository.findByCode(code)
                                .filter(vc -> vc.getUser().getId().equals(user.getId()))
                                .orElseThrow(() -> new BusinessException("Invalid reset code"));

                if (verificationCode.isExpired()) {
                        throw new BusinessException("Reset code has expired");
                }

                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                verificationCodeRepository.delete(verificationCode);

                log.info("Password reset successful for user: {}", email);
        }

        private UserResponse mapToResponse(User user) {
                return new UserResponse(
                                user.getId(),
                                user.getEmail(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getPhone(),
                                user.getRole().name(),
                                user.getActive());
        }
}
