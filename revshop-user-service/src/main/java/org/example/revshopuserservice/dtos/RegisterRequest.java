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

    private String securityQuestion;
    private String securityAnswer;
    private String businessDetails;

    public String getBusinessDetails() {
        return businessDetails;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setSecurityQuestion(String securityQuestion) { this.securityQuestion = securityQuestion; }
    public void setSecurityAnswer(String securityAnswer) { this.securityAnswer = securityAnswer; }
    public void setBusinessDetails(String businessDetails) { this.businessDetails = businessDetails; }
}
