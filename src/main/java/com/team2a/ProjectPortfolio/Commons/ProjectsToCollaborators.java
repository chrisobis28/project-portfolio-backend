package com.team2a.ProjectPortfolio.Commons;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name="PROJECTS_TO_COLLABORATORS")
public class ProjectsToCollaborators {
    @Id
    @Column(name="PTC_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private UUID ptcId;

    @ManyToOne
    @JoinColumn(name="COLLABORATOR_ID")
    @Getter
    @Setter
    private Collaborator collaborator;

    @ManyToOne
    @JoinColumn(name="PROJECT_ID")
    @Getter
    @Setter
    private Project project;

    /**
     * Constructor for the relation between the project and the collaborator
     * @param project the project of which the collaborator is part of
     * @param collaborator the collaborator for the project
     */
    public ProjectsToCollaborators(Project project,Collaborator collaborator) {
        this.collaborator = collaborator;
        this.project = project;
    }

}
