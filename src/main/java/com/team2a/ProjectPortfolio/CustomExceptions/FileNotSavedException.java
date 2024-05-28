package com.team2a.ProjectPortfolio.CustomExceptions;

public class FileNotSavedException extends RuntimeException {

    public FileNotSavedException (String message) {
        super(message);
    }
}
