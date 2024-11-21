package com.example.note_app.controller;

import com.example.note_app.entity.Role;
import com.example.note_app.entity.User;
import com.example.note_app.model.*;
import com.example.note_app.repository.RoleRepository;
import com.example.note_app.repository.UserRepository;
import com.example.note_app.security.jwt.JwtUtils;
import com.example.note_app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

//    private final JwtUtils jwtUtils;
//    private final AuthenticationManager authenticationManager;
//    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
    private final UserService userService;
//    PasswordEncoder passwordEncoder;

    @PostMapping("/public/signin")
    public ResponseEntity<?> userSignIn(@RequestBody LoginRequest request){

        Object response = userService.signInUser(request);
        if(response instanceof Map<?,?>){
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<LoginResponse>((LoginResponse) response, HttpStatus.OK);
    }

    @PostMapping("/public/signup")
    public ResponseEntity<?> userSignUp(@Valid @RequestBody SignupRequest newuser){

        String msg = null;
        try {
            msg = userService.registerUser(newuser);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
        if(msg != null && !msg.isEmpty() && msg.startsWith("Error")){
            return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(new MessageResponse(msg));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal UserDetails userDetails){
        User user = userService.findByUsername(userDetails.getUsername());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(r -> r.getAuthority())
                .collect(Collectors.toList());
        UserInfoResponse userInfoResponse = UserInfoResponse.builder()
                .id(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .accountNonLocked(user.getAccountNonLocked())
                .accountNonExpired(user.getAccountNonExpired())
                .credentialsNonExpired(user.getCredentialsNonExpired())
                .enabled(user.getEnabled())
                .isTwoFactorEnabled(user.getIsTwoFactorEnabled())
                .credentialsExpiryDate(user.getCredentialsExpiryDate())
                .accountExpiryDate(user.getAccountExpiryDate())
                .roles(roles).build();

        return ResponseEntity.ok(userInfoResponse);
    }

    @GetMapping("/username")
    public String getUsername(@AuthenticationPrincipal UserDetails userDetails){
        return userDetails.getUsername() != null ? userDetails.getUsername() : "";
    }

    @PostMapping("/public/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email){
        System.out.println("forgotPassword "+email);
        try {
            userService.userPasswordResetRequest(email);
            return ResponseEntity.ok(new MessageResponse("Password reset email sent!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse("Error sending password reset email"));
        }
    }

    @PostMapping("/public/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword){

        try {
            userService.userPasswordReset(token, newPassword);
            return ResponseEntity.ok(new MessageResponse("Password reset successful!"));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        }
    }
}
