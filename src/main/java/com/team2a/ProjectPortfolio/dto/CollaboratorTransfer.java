package com.team2a.ProjectPortfolio.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

public class CollaboratorTransfer {

    @Getter
    @Setter
    private UUID collaboratorId;

    @Getter
    @NotNull(message = "Name must be specified.")
    @Pattern(regexp = "^[a-zA-Z ]{1,50}$",
        message = "Name must consist of alphanumeric characters only, be separated by spaces " +
            "and be between 1 to 50 characters long.")
    private String name;

    @Getter
    @NotNull(message = "Role must be specified.")
    private String role;

    /**
     * Constructor for the collaborator transfer object
     * @param collaboratorId the collaborator id
     * @param name the name of the collaborator
     * @param role the role of the collaborator
     */
    public CollaboratorTransfer(
        UUID collaboratorId,
        String name,
        String role
    ) {
        this.collaboratorId = collaboratorId;
        this.name = name;
        this.role = role;
    }

}
