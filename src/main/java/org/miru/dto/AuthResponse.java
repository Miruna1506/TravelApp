package org.miru.dto;

public class AuthResponse {

    private String token;
    private String email;
    private String fullName;

    public AuthResponse() {
    }

    public AuthResponse(String token, String email, String fullName) {
        this.token = token;
        this.email = email;
        this.fullName = fullName;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }
}