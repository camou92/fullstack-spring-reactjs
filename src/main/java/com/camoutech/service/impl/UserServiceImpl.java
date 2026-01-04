package com.camoutech.service.impl;

import com.camoutech.mapper.UserMapper;
import com.camoutech.modal.User;
import com.camoutech.payload.dto.UserDTO;
import com.camoutech.repository.UserRepository;
import com.camoutech.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 02/01/2026
 */

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getCurrentUser() throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new Exception("user not found!");
        }
        return user;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream().map(
                UserMapper::toDTO
        ).collect(Collectors.toList());
    }
}
