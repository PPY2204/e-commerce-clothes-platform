package com.ecommerce.user.controller;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.user.dto.*;
import com.ecommerce.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@RequestParam String email, @RequestParam String code) {
        userService.verifyOtp(email, code);
        return ResponseEntity.ok(ApiResponse.success("Account activated successfully", null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam String email) {
        userService.forgotPassword(email);
        return ResponseEntity.ok(ApiResponse.success("Password reset code sent to your email", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam String email, 
            @RequestParam String code, 
            @RequestParam String newPassword) {
        userService.resetPassword(email, code, newPassword);
        return ResponseEntity.ok(ApiResponse.success("Password updated successfully", null));
    }
}
