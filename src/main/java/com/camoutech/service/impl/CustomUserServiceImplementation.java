package com.camoutech.service.impl;

import com.camoutech.modal.User;
import com.camoutech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 01/01/2026
 */

@Service
@RequiredArgsConstructor
public class CustomUserServiceImplementation implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("user not exist with username: " + username);
        }

        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().toString());

        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(authority);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), authorities
        );
    }
}
