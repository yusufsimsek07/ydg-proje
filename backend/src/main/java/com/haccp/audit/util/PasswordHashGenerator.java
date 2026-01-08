package com.haccp.audit.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class to generate bcrypt password hashes.
 * Run this main method to generate correct hashes for seed data.
 */
public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("Admin123!: " + encoder.encode("Admin123!"));
        System.out.println("Auditor123!: " + encoder.encode("Auditor123!"));
        System.out.println("Manager123!: " + encoder.encode("Manager123!"));
    }
}
