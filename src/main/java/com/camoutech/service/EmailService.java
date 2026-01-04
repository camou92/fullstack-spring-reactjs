package com.camoutech.service;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 01/01/2026
 */
public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
