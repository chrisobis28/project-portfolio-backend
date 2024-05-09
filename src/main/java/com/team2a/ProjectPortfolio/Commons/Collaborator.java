package com.team2a.ProjectPortfolio.Commons;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="COLLABORATOR")
public class Collaborator {
    @Id
    @Column(name="COLLABORATOR_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private UUID collaboratorId;

    @Column(name="NAME")
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "collaborator")
    @OnDelete(action= OnDeleteAction.CASCADE)
    private List<ProjectsToCollaborators> projectsToCollaborators;

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="COLLABORATOR_ID")
    private List<RequestCollaboratorsProjects> requestCollaboratorsProjects;

    /**
     * Constructor for the collaborator
     * @param name the collaborator's name
     */
    public Collaborator(String name) {
        this.name = name;
        this.projectsToCollaborators = new ArrayList<>();
    }
}
