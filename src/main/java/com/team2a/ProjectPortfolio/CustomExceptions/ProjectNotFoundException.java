package com.team2a.ProjectPortfolio.CustomExceptions;

public class ProjectNotFoundException extends RuntimeException{

    public ProjectNotFoundException(String message) {
        super(message);
    }
}
