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
import org.springframework.http.HttpStatus;
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
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;
    PasswordEncoder passwordEncoder;

    @PostMapping("/public/signin")
    public ResponseEntity<?> userSignIn(@RequestBody LoginRequest request){

        Authentication authentication;

        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        }catch (AuthenticationException e){
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Bad Credentials");
            response.put("status", false);
            return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        List<String> roles = authentication.getAuthorities().stream()
                .map(r -> r.getAuthority())
                .collect(Collectors.toList());

        return new ResponseEntity<LoginResponse>(new LoginResponse(userDetails.getUsername(), roles, jwtToken), HttpStatus.OK);
    }

    @PostMapping("/public/signup")
    public ResponseEntity<?> userSignUp(@Valid @RequestBody SignupRequest newuser){

        if(userRepository.existsByUsername(newuser.getUsername())){
            return new ResponseEntity<>("Error: Username already exist", HttpStatus.BAD_REQUEST);
        }
        if(userRepository.existsByEmail(newuser.getEmail())){
            return new ResponseEntity<>("Error: Email already taken", HttpStatus.BAD_REQUEST);
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
            return new ResponseEntity<>("Error: User could not be created!", HttpStatus.EXPECTATION_FAILED);
        }
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
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
