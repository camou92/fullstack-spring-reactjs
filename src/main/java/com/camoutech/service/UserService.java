package com.camoutech.service;

import com.camoutech.modal.User;
import com.camoutech.payload.dto.UserDTO;

import java.util.List;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 02/01/2026
 */
public interface UserService {

    public User getCurrentUser() throws Exception;
    public List<UserDTO> getAllUsers();
}
