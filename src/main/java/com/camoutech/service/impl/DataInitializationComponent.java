package com.camoutech.service.impl;

import com.camoutech.domain.UserRole;
import com.camoutech.modal.User;
import com.camoutech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 02/01/2026
 */

@Component
@RequiredArgsConstructor
public class DataInitializationComponent implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private void initializeAdminUser() {
        String adminEmail = "camoutech01@gmail.com";
        String adminPassword = "12345678";

        if (userRepository.findByEmail(adminEmail) == null) {
            User user = User.builder()
                    .password(passwordEncoder.encode(adminPassword))
                    .email(adminEmail)
                    .fullName("Mohamed Camara")
                    .role(UserRole.ROLE_ADMIN)
                    .build();

            User admin = userRepository.save(user);
        }
    }

    @Override
    public void run(String... args) throws Exception {
        initializeAdminUser();
    }
}
