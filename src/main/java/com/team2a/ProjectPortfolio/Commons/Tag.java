package com.team2a.ProjectPortfolio.Commons;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "TAG")
public class Tag {
    @Id
    @Column(name="TAG_ID", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private UUID tagId;

    @Column(name="NAME")
    @Getter
    @Setter
    private String name;

    @Column(name="COLOR")
    @Getter
    @Setter
    private String color;


    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action=OnDeleteAction.CASCADE)
    @JoinColumn(name="TAG_ID")
    private List<RequestTagProject> requestTagProjects;

    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action=OnDeleteAction.CASCADE)
    @JoinColumn(name="TAG_ID")
    private List<TagsToProject> tagsToProjects;

    public Tag(UUID tagId, String name, String color, List<RequestTagProject> requestTagProjects, List<TagsToProject> tagsToProjects) {
        this.tagId = tagId;
        this.name = name;
        this.color = color;
        this.requestTagProjects = requestTagProjects;
        this.tagsToProjects = tagsToProjects;
    }
}
