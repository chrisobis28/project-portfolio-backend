package com.team2a.ProjectPortfolio.Commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "REQUEST_COLLABORATORS_PROJECTS")
@ToString
@NoArgsConstructor
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
    @JoinColumn(name = "COLLABORATOR_ID")
    @Getter
    @Setter
    @JsonIgnore
    private Collaborator collaborator;

    public RequestCollaboratorsProjects(UUID id, Boolean isRemove) {
        this.id = id;
        this.isRemove = isRemove;
    }

    public RequestCollaboratorsProjects(Collaborator collaborator) {
        this.collaborator = collaborator;
    }
}
