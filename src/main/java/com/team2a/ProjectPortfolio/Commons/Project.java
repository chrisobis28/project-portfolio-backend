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
@Table(name = "PROJECT")
public class Project {
    @Id
    @Column(name="PROJECT_ID", nullable=false)
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

    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action=OnDeleteAction.CASCADE)
    @JoinColumn(name="PROJECT_ID")
    private List<Media> media;

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action=OnDeleteAction.CASCADE)
    @JoinColumn(name="PROJECT_ID")
    private List<ProjectsToAccounts> projectsToAccounts;


    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "project")
    @OnDelete(action=OnDeleteAction.CASCADE)
    private List<ProjectsToCollaborators> projectsToCollaborators;

    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action=OnDeleteAction.CASCADE)
    @JoinColumn(name="PROJECT_ID")
    private List<TagsToProject> tagsToProjects;

    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action=OnDeleteAction.CASCADE)
    @JoinColumn(name="PROJECT_ID")
    private List<Link> links;

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="REQUEST_PROJECT")
    private List<Request> requests;

    public Project(String title, String description, String bibtex, Boolean archived, List<Media> media,
                    List<Link> links) {
        this.title = title;
        this.description = description;
        this.bibtex = bibtex;
        this.archived = archived;
        this.media = media;
        this.projectsToAccounts = new ArrayList<>();
        this.projectsToCollaborators = new ArrayList<>();
        this.tagsToProjects = new ArrayList<>();
        this.links = links;
    }
}
