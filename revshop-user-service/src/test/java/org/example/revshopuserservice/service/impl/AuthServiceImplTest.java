package org.example.revshopuserservice.service.impl;

import org.example.revshopuserservice.dtos.AuthResponse;
import org.example.revshopuserservice.dtos.LoginRequest;
import org.example.revshopuserservice.model.User;
import org.example.revshopuserservice.security.JwtProvider;
import org.example.revshopuserservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1);
        testUser.setEmail("test@test.com");
        testUser.setPassword("password");
        testUser.setRole("USER");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("password");
    }

    @Test
    void testLogin_Success() {
        when(userService.login("test@test.com", "password")).thenReturn(testUser);
        when(jwtProvider.generateToken("test@test.com", "USER", 1)).thenReturn("mockedToken");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mockedToken", response.getToken());
        assertEquals("USER", response.getRole());
        assertEquals(1, response.getUserId());
    }
}
