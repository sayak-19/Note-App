package com.example.note_app.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class PasswordEncoding {

    public static void main(String[] args) {
        String password1 = "admin";
        String password2 = "john";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode(password1));
        System.out.println(passwordEncoder.encode(password2));
    }
}
