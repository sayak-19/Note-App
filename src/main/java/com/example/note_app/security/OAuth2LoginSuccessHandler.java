package com.example.note_app.security;

import com.example.note_app.entity.Role;
import com.example.note_app.entity.User;
import com.example.note_app.model.AppRole;
import com.example.note_app.security.jwt.JwtUtils;
import com.example.note_app.security.services.CustomUserDetails;
import com.example.note_app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Value("${frontend.url}")
    String frontendUrl;

    private final UserService userService;
    private final JwtUtils jwtUtils;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String username, idAttributeKey;

        if(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId() != null &&
                (oAuth2AuthenticationToken.getAuthorizedClientRegistrationId().equals("google") || oAuth2AuthenticationToken.getAuthorizedClientRegistrationId().equals("github"))) {

            DefaultOAuth2User principal = (DefaultOAuth2User) oAuth2AuthenticationToken.getPrincipal();
            Map<String, Object> attributes = principal.getAttributes();
            String email = (String) attributes.getOrDefault("email", "");
            String name = (String) attributes.getOrDefault("name", "");
            if ("github".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
                username = attributes.getOrDefault("login", "").toString();
                idAttributeKey = "id";
            } else if ("google".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
                username = email.split("@")[0];
                idAttributeKey = "sub";
            } else {
                username = "";
                idAttributeKey = "id";
            }
            System.out.println("OAuth User : " + email + " : " + name + " : " + username);

            userService.findByEmail(email)
                    .ifPresentOrElse(user -> {
                        DefaultOAuth2User defaultOAuth2User = new DefaultOAuth2User(
                                List.of(new SimpleGrantedAuthority(user.getRole().getRoleName().name())), attributes, idAttributeKey
                        );
                        Authentication auth = new OAuth2AuthenticationToken(defaultOAuth2User,
                                List.of(new SimpleGrantedAuthority(user.getRole().getRoleName().name())), oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }, () -> {
                        User newuser = new User();
                        Role userRole = userService.findByRoleName(AppRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Default role not found"));
                        newuser.setRole(userRole);
                        newuser.setEmail(email);
                        newuser.setUsername(username);
                        newuser.setSignUpMethod(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
                        userService.registerUser(newuser);
                        DefaultOAuth2User defaultOAuth2User = new DefaultOAuth2User(
                                List.of(new SimpleGrantedAuthority(newuser.getRole().getRoleName().name())), attributes, idAttributeKey
                        );
                        Authentication auth = new OAuth2AuthenticationToken(defaultOAuth2User,
                                List.of(new SimpleGrantedAuthority(newuser.getRole().getRoleName().name())), oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    });

            this.setAlwaysUseDefaultTargetUrl(true);

            //DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
            //Map<String, Object> attributes = defaultOAuth2User.getAttributes();
            //String email = (String) attributes.getOrDefault("email", "");
            System.out.println("OAuth2LoginSuccessHandler: " + username + " : " + email);
            Set<SimpleGrantedAuthority> authorities = new HashSet<>(principal.getAuthorities().stream()
                    .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                    .collect(Collectors.toList()));
            User user = userService.findByEmail(email).orElseThrow(
                    ()->new EntityNotFoundException("User not found"));
            authorities.add(new SimpleGrantedAuthority(user.getRole().getRoleName().name()));

            CustomUserDetails userDetails = new CustomUserDetails(
                    null,
                    username,
                    email,
                    null,
                    false,
                    authorities
            );
            String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

            String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl+"/oauth2/redirect")
                    .queryParam("token", jwtToken)
                    .build().toString();

            this.setDefaultTargetUrl(targetUrl);
            super.onAuthenticationSuccess(request,response,authentication);
        }
    }
}
