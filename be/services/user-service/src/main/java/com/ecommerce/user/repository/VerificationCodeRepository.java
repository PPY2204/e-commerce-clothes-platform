package com.ecommerce.user.repository;

import com.ecommerce.user.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByCode(String code);
    Optional<VerificationCode> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
