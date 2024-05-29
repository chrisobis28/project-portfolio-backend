package com.team2a.ProjectPortfolio.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

public class RegisterUserRequest {

    @Getter
    @NotNull(message = "Username must be specified.")
    @Pattern(regexp = "^[a-zA-Z0-9]{5,20}$", message = "Username must consist of alphanumeric characters only " +
        "and be between 5 to 20 characters long.")
    private String username;

    @Getter
    @NotNull(message = "Password must be specified.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", message = "Password must be at least 8 characters long " +
        "and contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace.")
    private String password;

    @Getter
    @NotNull(message = "Name must be specified.")
    @Pattern(regexp = "^[a-zA-Z_]{1,50}$", message = "Name must consist of alphanumeric characters only, be separated by _ " +
        "and be between 1 to 50 characters long.")
    private String name;

    /**
     * Constructor for the Register User Request
     * @param username - the username
     * @param password - the password
     * @param name - the name
     */
    public RegisterUserRequest(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }
}
