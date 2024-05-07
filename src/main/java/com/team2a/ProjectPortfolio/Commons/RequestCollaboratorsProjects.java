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

    public RequestCollaboratorsProjects(UUID id, Boolean isRemove) {
        this.id = id;
        this.isRemove = isRemove;
    }
}
