package com.camoutech.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 02/01/2026
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {

    private String token;
    private String password;
}
