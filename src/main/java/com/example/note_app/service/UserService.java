package com.example.note_app.service;

import com.example.note_app.entity.PasswordResetToken;
import com.example.note_app.entity.Role;
import com.example.note_app.entity.User;
import com.example.note_app.model.AppRole;
import com.example.note_app.model.LoginRequest;
import com.example.note_app.model.LoginResponse;
import com.example.note_app.model.SignupRequest;
import com.example.note_app.repository.PasswordResetTokenRepository;
import com.example.note_app.repository.RoleRepository;
import com.example.note_app.repository.UserRepository;
import com.example.note_app.security.jwt.JwtUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${frontend.url}")
    String frontendUrl;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public String registerUser(SignupRequest newuser)throws RuntimeException{
        if(userRepository.existsByUsername(newuser.getUsername())){
            return ("Error: Username already exist");
        }
        if(userRepository.existsByEmail(newuser.getEmail())){
            return ("Error: Email already taken");
        }
        User user = new User(newuser.getUsername(), newuser.getEmail(), passwordEncoder.encode(newuser.getPassword()));
        Role role = roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseThrow(() -> new EntityNotFoundException("Error: Role not found"));

        user.setRole(role);
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
        user.setAccountExpiryDate(LocalDate.now().plusYears(1));
        user.setSignUpMethod("email");

        user = userRepository.save(user);
        if(user == null){
            //return new ResponseEntity<>("Error: User could not be created!", HttpStatus.EXPECTATION_FAILED);
            throw new RuntimeException("Error: User could not be created!");
        }
        return "User registered successfully!";
    }

    public User registerUser(User user){
        if (user.getPassword() != null)
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Object signInUser(LoginRequest request){
        Authentication authentication;

        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        }catch (AuthenticationException e){
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Bad Credentials");
            response.put("status", false);
            return response;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        List<String> roles = authentication.getAuthorities().stream()
                .map(r -> r.getAuthority())
                .collect(Collectors.toList());
        return new LoginResponse(userDetails.getUsername(), roles, jwtToken);
    }

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

    public void userPasswordReset(String token, String newPassword){
        PasswordResetToken tokenObj = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(()-> new RuntimeException("Invalid password reset request"));
        if(tokenObj.isUsed()){
            throw new RuntimeException("Password reset token is used");
        }
        if(tokenObj.getExpiryDate().isBefore(Instant.now())){
            throw new RuntimeException("Password reset token is expired");
        }
        User user = tokenObj.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenObj.setUsed(true);
        passwordResetTokenRepository.save(tokenObj);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public Optional<Role> findByRoleName(AppRole role){
        return roleRepository.findByRoleName(role);
    }
}
