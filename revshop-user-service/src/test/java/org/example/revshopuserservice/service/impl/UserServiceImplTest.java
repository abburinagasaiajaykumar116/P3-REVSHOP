package org.example.revshopuserservice.service.impl;

import org.example.revshopuserservice.exception.BadRequestException;
import org.example.revshopuserservice.exception.ResourceNotFoundException;
import org.example.revshopuserservice.exception.UnauthorizedException;
import org.example.revshopuserservice.model.User;
import org.example.revshopuserservice.repos.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1);
        testUser.setEmail("test@test.com");
        testUser.setPassword("password");
        testUser.setSecurityQuestion("mother's maiden name?");
        testUser.setSecurityAnswer("smith");
    }

    @Test
    void testRegister_Success() {
        when(repo.findByEmail(anyString())).thenReturn(Optional.empty());
        when(encoder.encode(anyString())).thenReturn("encodedPassword");

        userService.register(testUser);

        verify(repo, times(1)).save(testUser);
        assertEquals("encodedPassword", testUser.getPassword());
    }

    @Test
    void testRegister_NullUser_ThrowsException() {
        assertThrows(BadRequestException.class, () -> userService.register(null));
    }

    @Test
    void testRegister_EmptyEmail_ThrowsException() {
        testUser.setEmail("");
        assertThrows(BadRequestException.class, () -> userService.register(testUser));
    }

    @Test
    void testRegister_ShortPassword_ThrowsException() {
        testUser.setPassword("123");
        assertThrows(BadRequestException.class, () -> userService.register(testUser));
    }

    @Test
    void testRegister_DuplicateEmail_ThrowsException() {
        when(repo.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        assertThrows(BadRequestException.class, () -> userService.register(testUser));
    }

    @Test
    void testGetByEmail_Success() {
        when(repo.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        User result = userService.getByEmail("test@test.com");
        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
    }

    @Test
    void testGetByEmail_NotFound_ThrowsException() {
        when(repo.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getByEmail("notfound@test.com"));
    }

    @Test
    void testGetById_Success() {
        when(repo.findById(1)).thenReturn(Optional.of(testUser));
        User result = userService.getById(1);
        assertNotNull(result);
        assertEquals(1, result.getUserId());
    }

    @Test
    void testGetById_NullId_ThrowsException() {
        assertThrows(BadRequestException.class, () -> userService.getById(null));
    }

    @Test
    void testGetById_NotFound_ThrowsException() {
        when(repo.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getById(99));
    }

    @Test
    void testLogin_Success() {
        when(repo.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(encoder.matches("password", testUser.getPassword())).thenReturn(true);

        User result = userService.login("test@test.com", "password");
        assertNotNull(result);
        assertEquals(1, result.getUserId());
    }

    @Test
    void testLogin_EmptyEmail_ThrowsException() {
        assertThrows(BadRequestException.class, () -> userService.login("", "password"));
    }

    @Test
    void testLogin_EmptyPassword_ThrowsException() {
        assertThrows(BadRequestException.class, () -> userService.login("test@test.com", ""));
    }

    @Test
    void testLogin_InvalidEmail_ThrowsException() {
        when(repo.findByEmail("invalid@test.com")).thenReturn(Optional.empty());
        assertThrows(UnauthorizedException.class, () -> userService.login("invalid@test.com", "password"));
    }

    @Test
    void testLogin_InvalidPassword_ThrowsException() {
        when(repo.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(encoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userService.login("test@test.com", "wrongpassword"));
    }

    @Test
    void testChangePassword_Success() {
        when(repo.findById(1)).thenReturn(Optional.of(testUser));
        when(encoder.encode("newpassword")).thenReturn("encodedNewPassword");

        userService.changePassword(1, "newpassword");

        verify(repo, times(1)).save(testUser);
        assertEquals("encodedNewPassword", testUser.getPassword());
    }

    @Test
    void testChangePassword_ShortPassword_ThrowsException() {
        assertThrows(BadRequestException.class, () -> userService.changePassword(1, "123"));
    }

    @Test
    void testChangePassword_UserNotFound_ThrowsException() {
        when(repo.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.changePassword(1, "newpassword"));
    }

    @Test
    void testForgotPassword_Success() {
        when(repo.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(encoder.encode("newpassword")).thenReturn("encodedNewPassword");

        userService.forgotPassword("test@test.com", "smith", "newpassword");

        verify(repo, times(1)).save(testUser);
        assertEquals("encodedNewPassword", testUser.getPassword());
    }

    @Test
    void testForgotPassword_InvalidAnswer_ThrowsException() {
        when(repo.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        assertThrows(UnauthorizedException.class, () -> userService.forgotPassword("test@test.com", "wronganswer", "newpassword"));
    }

    @Test
    void testGetSecurityQuestion_Success() {
        when(repo.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        String result = userService.getSecurityQuestion("test@test.com");
        assertEquals("mother's maiden name?", result);
    }
}
