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


    public ProjectsToCollaborators(UUID ptcId) {
        this.ptcId = ptcId;
    }
}
