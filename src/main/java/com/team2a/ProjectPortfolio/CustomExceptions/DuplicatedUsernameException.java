package com.team2a.ProjectPortfolio.CustomExceptions;

public class DuplicatedUsernameException extends RuntimeException {

    public DuplicatedUsernameException(String message) {
        super(message);
    }
}
