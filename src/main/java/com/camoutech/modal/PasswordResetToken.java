package com.camoutech.modal;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 01/01/2026
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }
}
