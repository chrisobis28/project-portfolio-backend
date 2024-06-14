package com.team2a.ProjectPortfolio.dto;

import com.team2a.ProjectPortfolio.Commons.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class AccountTransfer {

    @Getter
    @NotNull (message = "Username must be specified")
    private String username;

    @Getter
    @NotNull (message = "isPM required")
    private boolean PM;

    @Getter
    @NotNull (message = "isAdmin required")
    private boolean isAdmin;

    /**
     * Constructor for the Account Transfer DTO
     * @param username - the username of the Account
     * @param isPM - Account is PM
     * @param isAdmin - Account is Admin
     */
    public AccountTransfer (String username, boolean isPM, boolean isAdmin) {
        this.username = username;
        this.PM = isPM;
        this.isAdmin = isAdmin;
    }
}
