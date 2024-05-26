package com.team2a.ProjectPortfolio.Commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name="PROJECTS_TO_COLLABORATORS")
@AllArgsConstructor
@NoArgsConstructor
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
    @JsonIgnore
    private Collaborator collaborator;

    @ManyToOne
    @JoinColumn(name="PROJECT_ID")
    @Getter
    @Setter
    @JsonIgnore
    private Project project;

    @Getter
    @Setter
    @Column(name = "Role")
    private String role;

    /**
     * Constructor for the relation between the project and the collaborator
     * @param project the project of which the collaborator is part of
     * @param collaborator the collaborator for the project
     * @param role the role of the collaborator
     */
    public ProjectsToCollaborators(Project project,Collaborator collaborator,String role) {
        this.collaborator = collaborator;
        this.project = project;
        this.role = role;
    }

}
