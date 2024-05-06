package com.team2a.ProjectPortfolio.Commons;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "PROJECT")
public class Project {
    @Id
    @Column(name="PROJECTID", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private UUID projectId;

    @Column(name="TITLE")
    @Getter
    @Setter
    private String title;

    @Column(name="DESCRIPTION")
    @Getter
    @Setter
    private String description;

    @Column(name="BIBTEX")
    @Getter
    @Setter
    private String bibtex;

    @Column(name="ARCHIVED")
    @Getter
    @Setter
    private Boolean archived;

    public Project(UUID projectId, String title, String description, String bibtex, Boolean archived) {
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.bibtex = bibtex;
        this.archived = archived;
    }
}
