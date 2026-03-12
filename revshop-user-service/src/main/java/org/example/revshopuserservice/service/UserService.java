package org.example.revshopuserservice.service;

import org.example.revshopuserservice.exception.BadRequestException;
import org.example.revshopuserservice.exception.ResourceNotFoundException;
import org.example.revshopuserservice.exception.UnauthorizedException;
import org.example.revshopuserservice.model.User;

public interface UserService {

    public void register(User user) ;
    User getById(Integer userId);

    public User getByEmail(String email) ;

    public User login(String email, String password);

    public void changePassword(Integer userId, String newPassword) ;

    public void forgotPassword(String email, String answer, String newPassword) ;


    public String getSecurityQuestion(String email);
}
