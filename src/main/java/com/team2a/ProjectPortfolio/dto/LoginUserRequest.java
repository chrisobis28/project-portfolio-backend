package com.team2a.ProjectPortfolio.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class LoginUserRequest {

    @Getter
    @NotNull (message = "Username must be specififed")
    private String username;

    @Getter
    @NotNull (message = "Password must be specified")
    private String password;

    /**
     * Constructor for the Login User Request
     * @param username - the username of the user
     * @param password - the password of the user
     */
    public LoginUserRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
