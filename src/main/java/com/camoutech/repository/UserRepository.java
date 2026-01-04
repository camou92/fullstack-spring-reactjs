package com.camoutech.repository;

import com.camoutech.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 01/01/2026
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}
