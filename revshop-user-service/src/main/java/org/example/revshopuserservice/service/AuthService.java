package org.example.revshopuserservice.service;

import org.example.revshopuserservice.dtos.AuthResponse;
import org.example.revshopuserservice.dtos.LoginRequest;
import org.example.revshopuserservice.model.User;

public interface AuthService {

    public AuthResponse login(LoginRequest request);
}
