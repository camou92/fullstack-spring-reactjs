package com.camoutech.payload.response;

import com.camoutech.modal.User;
import com.camoutech.payload.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 01/01/2026
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String title;
    private String jwt;
    private String message;
    private UserDTO user;
}
