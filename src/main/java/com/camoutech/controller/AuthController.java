package com.camoutech.controller;

import com.camoutech.exception.UserException;
import com.camoutech.payload.dto.UserDTO;
import com.camoutech.payload.request.ForgotPasswordRequest;
import com.camoutech.payload.request.LoginRequest;
import com.camoutech.payload.request.ResetPasswordRequest;
import com.camoutech.payload.response.ApiResponse;
import com.camoutech.payload.response.AuthResponse;
import com.camoutech.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 02/01/2026
 */

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signupHandler(
            @RequestBody @Valid UserDTO req) throws UserException {
        AuthResponse res = authService.signup(req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginHandler(
            @RequestBody @Valid LoginRequest req) throws UserException {
        AuthResponse res = authService.login(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(
            @RequestBody ForgotPasswordRequest request) throws UserException {
        authService.createPasswordResetToken(request.getEmail());

        ApiResponse res = new ApiResponse(
                "A Reset link was sent to your email. ", true
        );
        return ResponseEntity.ok(res);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(
            @RequestBody ResetPasswordRequest request) throws Exception {
        authService.resetPassword(request.getToken(), request.getPassword());

        ApiResponse res = new ApiResponse("Password reset successfully", true);

        return ResponseEntity.ok(res);
    }
}
