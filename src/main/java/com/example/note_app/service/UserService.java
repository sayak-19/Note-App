package com.example.note_app.service;

import com.example.note_app.entity.PasswordResetToken;
import com.example.note_app.entity.User;
import com.example.note_app.repository.PasswordResetTokenRepository;
import com.example.note_app.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${frontend.url}")
    String frontendUrl;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    public User findByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Username "+username+" not found"));
    }

    public void userPasswordResetRequest(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Email "+email+" not found"));

        String token = UUID.randomUUID().toString();

        passwordResetTokenRepository.save(PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(Instant.now().plus(24, ChronoUnit.HOURS)).build());

        String resetURL = frontendUrl + "/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(resetURL, email);
    }
}
