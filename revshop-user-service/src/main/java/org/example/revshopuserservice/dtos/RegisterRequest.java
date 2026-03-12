package org.example.revshopuserservice.dtos;
public class RegisterRequest {

    private String name;
    private String email;
    private String password;
    private String role;

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    private String securityAnswer;
}
