package org.miru.dto;

public class RegisterRequest {

    private String fullName;
    private String email;
    private String password;

    public RegisterRequest() {
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}