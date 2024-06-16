package com.team2a.ProjectPortfolio.dto;

import com.team2a.ProjectPortfolio.Commons.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class AccountTransfer {

    @Getter
    @NotNull (message = "Username must be specified")
    private String username;

    @Getter
    @NotNull (message = "Role required")
    private Role role;

    /**
     * Constructor for the Account Transfer DTO
     * @param username - the username of the account
     * @param role - the role of the account
     */
    public AccountTransfer (String username, Role role) {
        this.username = username;
        this.role = role;
    }
}
