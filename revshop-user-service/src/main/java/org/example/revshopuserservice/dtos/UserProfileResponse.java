package org.example.revshopuserservice.dtos;

public class UserProfileResponse {

    private String name;
    private String email;
    private String role;
    private String businessDetails;

    public UserProfileResponse(String name, String email, String role, String businessDetails) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.businessDetails = businessDetails;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getBusinessDetails() { return businessDetails; }
}
