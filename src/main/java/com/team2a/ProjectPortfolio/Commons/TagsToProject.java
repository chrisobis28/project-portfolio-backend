package com.team2a.ProjectPortfolio.Commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "TAGS_TO_PROJECT")
@NoArgsConstructor
public class TagsToProject {

    @ManyToOne
    @JoinColumn(name="TAG_ID")
    @Getter
    @Setter
    @JsonIgnore
    private Tag tag;

    @ManyToOne
    @JoinColumn(name="PROJECT_ID")
    @Getter
    @Setter
    @JsonIgnore
    private Project project;

    @Id
    @Column(name="TAG_TO_PROJECT_ID", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private UUID tagToProjectId;


    /**
     * Constructor for the tags to project
     * @param tag the tag
     * @param project the project
     */
    public TagsToProject(Tag tag, Project project) {
        this.tag = tag;
        this.project = project;
    }
}
