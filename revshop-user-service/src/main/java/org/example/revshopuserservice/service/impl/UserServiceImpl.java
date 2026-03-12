package org.example.revshopuserservice.service.impl;

import org.example.revshopuserservice.exception.BadRequestException;
import org.example.revshopuserservice.exception.ResourceNotFoundException;
import org.example.revshopuserservice.exception.UnauthorizedException;
import org.example.revshopuserservice.model.User;
import org.example.revshopuserservice.repos.UserRepository;
import org.example.revshopuserservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    public void register(User user) {

        if (user == null)
            throw new BadRequestException("User data is missing");

        if (user.getEmail() == null || user.getEmail().trim().isEmpty())
            throw new BadRequestException("Email cannot be empty");

        if (user.getPassword() == null || user.getPassword().length() < 4)
            throw new BadRequestException("Password must contain at least 4 characters");

        // duplicate email protection
        if (repo.findByEmail(user.getEmail()).isPresent())
            throw new BadRequestException("Email already registered");

        user.setPassword(encoder.encode(user.getPassword()));

        repo.save(user);
    }
    @Override
    public User getByEmail(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
    }
    @Override
    public User getById(Integer userId) {

        if (userId == null)
            throw new BadRequestException("User id is required");

        return repo.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id " + userId));
    }


    public User login(String email, String password) {

        if (email == null || email.trim().isEmpty())
            throw new BadRequestException("Email is required");

        if (password == null || password.trim().isEmpty())
            throw new BadRequestException("Password is required");

        User user = repo.findByEmail(email)
                .orElseThrow(() ->
                        new UnauthorizedException("Invalid email or password"));

        if (!encoder.matches(password, user.getPassword()))
            throw new RuntimeException("Invalid credentials");



        return user;
    }

    public void changePassword(Integer userId, String newPassword) {


        if (newPassword == null || newPassword.length() < 4)
            throw new BadRequestException("Password must contain at least 4 characters");

        User user = repo.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id " + userId));
        user.setPassword(encoder.encode(newPassword));
        repo.save(user);
    }

    public void forgotPassword(String email, String answer, String newPassword) {
        if (email == null || email.trim().isEmpty())
            throw new BadRequestException("Email is required");

        if (newPassword == null || newPassword.length() < 4)
            throw new BadRequestException("Password must contain at least 4 characters");

        User user = repo.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with email " + email));

        if (user.getSecurityAnswer() == null || !user.getSecurityAnswer().equals(answer))
            throw new UnauthorizedException("Incorrect security answer");
        user.setPassword(encoder.encode(newPassword));
        repo.save(user);
    }

    public String getSecurityQuestion(String email) {
        return repo.findByEmail(email)
                .map(User::getSecurityQuestion)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with email " + email));
    }
}



