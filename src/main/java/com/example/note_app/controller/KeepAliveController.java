package com.example.note_app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class KeepAliveController {

    private final JdbcTemplate jdbcTemplate;

    public KeepAliveController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/keep-alive")
    public ResponseEntity<Map<String, String>> keepAlive() {
        Integer userCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users", Integer.class
        );
        var response = new HashMap<String, String>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("userCount", String.valueOf(userCount));
        return ResponseEntity.ok(response);
    }
}
