package org.example.revshopuserservice.controller;

import org.example.revshopuserservice.dtos.AuthResponse;
import org.example.revshopuserservice.dtos.LoginRequest;
import org.example.revshopuserservice.dtos.RegisterRequest;
import org.example.revshopuserservice.model.User;
import org.example.revshopuserservice.service.AuthService;
import org.example.revshopuserservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        userService.register(user);

        return ResponseEntity.ok(
                Map.of("message", "User registered successfully")
        );
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}