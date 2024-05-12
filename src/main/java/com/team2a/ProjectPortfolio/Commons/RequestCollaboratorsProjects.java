package com.team2a.ProjectPortfolio.Commons;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "REQUEST_COLLABORATORS_PROJECTS")
public class RequestCollaboratorsProjects {

    @Id
    @Column(name="REQUEST_COLLABORATORS_PROJECTS_ID", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private UUID id;

    @Column(name = "IS_REMOVE")
    @Getter
    @Setter
    private Boolean isRemove;

    @ManyToOne
    @JoinColumn(name = "REQUEST_ID")
    @Getter
    @Setter
    private Request request;

    @ManyToOne
    @JoinColumn(name = "COLLABORATOR_ID")
    @Getter
    @Setter
    private Collaborator collaborator;

    public RequestCollaboratorsProjects(UUID id, Boolean isRemove) {
        this.id = id;
        this.isRemove = isRemove;
    }

    public RequestCollaboratorsProjects(Request request, Collaborator collaborator) {
        this.request = request;
        this.collaborator = collaborator;
    }
}
