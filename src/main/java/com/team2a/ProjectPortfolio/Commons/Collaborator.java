package com.team2a.ProjectPortfolio.Commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@NoArgsConstructor
@Entity
@Table(name="COLLABORATOR")
@Data
public class Collaborator {
    @Id
    @Column(name="COLLABORATOR_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    @JsonProperty
    private UUID collaboratorId;

    @Column(name="NAME")
    @Getter
    @Setter
    @JsonProperty
    private String name;

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="COLLABORATOR_ID")
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
