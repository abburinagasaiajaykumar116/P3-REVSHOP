package org.example.revshopuserservice.service.impl;

import org.example.revshopuserservice.dtos.AuthResponse;
import org.example.revshopuserservice.dtos.LoginRequest;
import org.example.revshopuserservice.model.User;

import org.example.revshopuserservice.security.JwtProvider;
import org.example.revshopuserservice.service.AuthService;
import org.example.revshopuserservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userService.login(request.getEmail(), request.getPassword());

        String token = jwtProvider.generateToken(
                user.getEmail(),
                user.getRole(),
                user.getUserId()
        );

        return new AuthResponse(
                token,
                user.getRole(),
                user.getUserId()   // 🔥 ADD THIS
        );
    }
}