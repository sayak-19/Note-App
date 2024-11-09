package com.example.note_app.controller;

import com.example.note_app.entity.AuditLog;
import com.example.note_app.entity.User;
import com.example.note_app.repository.UserRepository;
import com.example.note_app.service.AuditLogService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/getAllUsers")
    public ResponseEntity<List<User>> getAllUser(){
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/getAllUsers/{id}")
    public ResponseEntity<User> getUserById(@RequestParam String id){
        return ResponseEntity.ok(userRepository.findById(Long.parseLong(id)).orElseThrow(()-> new EntityNotFoundException("User not found!")));
    }

    @GetMapping(value = "/audit")
    public ResponseEntity<List<AuditLog>> audit(){
        return ResponseEntity.ok(auditLogService.fetchAllLog());
    }
}
