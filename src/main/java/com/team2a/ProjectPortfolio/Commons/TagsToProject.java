package com.team2a.ProjectPortfolio.Commons;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "TAGS_TO_PROJECT")
public class TagsToProject {

    @Id
    @Column(name="TAG_TO_PROJECT_ID", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private UUID tagToProjectId;

    public TagsToProject(UUID tagToProjectId) {
        this.tagToProjectId = tagToProjectId;
    }
}
