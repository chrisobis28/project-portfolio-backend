package com.team2a.ProjectPortfolio.Commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@NoArgsConstructor
@EqualsAndHashCode
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
    @Size(max = 4000)
    private String description;

    @Column(name="ARCHIVED")
    @Getter
    @Setter
    private Boolean archived;

    @ManyToOne
    @JoinColumn(name="TEMPLATE_NAME")
    @Getter
    @Setter
    @JsonIgnore
    private Template template;

    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action=OnDeleteAction.CASCADE)
    @JoinColumn(name="PROJECT_ID")
    @JsonIgnore
    private List<Media> media = new ArrayList<>();

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action=OnDeleteAction.CASCADE)
    @JoinColumn(name="PROJECT_ID")
    @JsonIgnore
    private List<ProjectsToAccounts> projectsToAccounts = new ArrayList<>();


    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action=OnDeleteAction.CASCADE)
    @JoinColumn(name="PROJECT_ID")
    private List<ProjectsToCollaborators> projectsToCollaborators = new ArrayList<>();

    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action=OnDeleteAction.CASCADE)
    @JoinColumn(name="PROJECT_ID")
    private List<TagsToProject> tagsToProjects = new ArrayList<>();

    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action=OnDeleteAction.CASCADE)
    @JoinColumn(name="PROJECT_ID")
    private List<Link> links = new ArrayList<>();

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="REQUEST_PROJECT")
    @JsonIgnore
    private List<Request> requests = new ArrayList<>();

    /**
     * Constructor for a project
     * @param title the title of the project
     * @param description the description of the project
     * @param archived archived
     */
    public Project(String title, String description, Boolean archived) {
        this.title = title;
        this.description = description;
        this.archived = archived;
    }

    /**
     * Constructor for a project
     * @param title the title of the project
     * @param description the description of the project
     * @param archived archived
     * @param template template
     */
    public Project(String title, String description, Boolean archived, Template template) {
        this.title = title;
        this.description = description;
        this.archived = archived;
        this.template = template;
    }
}
