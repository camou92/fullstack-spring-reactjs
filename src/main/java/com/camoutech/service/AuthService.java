package com.camoutech.service;

import com.camoutech.exception.UserException;
import com.camoutech.payload.dto.UserDTO;
import com.camoutech.payload.response.AuthResponse;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 01/01/2026
 */
public interface AuthService {

    AuthResponse login(String username, String password) throws UserException;
    AuthResponse signup(UserDTO req) throws UserException;

    void createPasswordResetToken(String email) throws UserException;
    void resetPassword(String token, String newPassword) throws Exception;
}
