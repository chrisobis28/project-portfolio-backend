package com.team2a.ProjectPortfolio.dto;

import lombok.Getter;

public class AccountDisplay {

    @Getter
    private String username;

    @Getter
    private String name;

    @Getter
    private String roleInProject;

    /**
     * Constructor for the AccountDisplay
     * @param username - the username of the account
     * @param name - the name of the account
     * @param roleInProject - the role of the account in the project
     */
    public AccountDisplay(String username, String name, String roleInProject) {
        this.username = username;
        this.name = name;
        this.roleInProject = roleInProject;
    }
}
