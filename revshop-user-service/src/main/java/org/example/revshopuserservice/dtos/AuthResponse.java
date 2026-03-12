package org.example.revshopuserservice.dtos;

public class AuthResponse {

    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    private String role;
    private Integer userId;

    public AuthResponse(String token, String role, Integer userId) {
        this.token = token;
        this.role = role;
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }

    public Integer getUserId() {
        return userId;
    }
}