package com.team2a.ProjectPortfolio.Exceptions;

public class NotFoundException extends RuntimeException {

    /**
     * Empty Constructor for NotFoundException
     */
    public NotFoundException() {
        super();
    }

    /**
     * Constructor with message for NotFoundException
     * @param message the message displayed by the exception
     */
    public NotFoundException(String message) {
        super(message);
    }

}
