package com.team2a.ProjectPortfolio.Commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "TAG")
@NoArgsConstructor
@Data
public class Tag {
    @Id
    @Column(name="TAG_ID", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    @JsonProperty
    private UUID tagId;

    @Column(name="NAME")
    @Getter
    @Setter
    @JsonProperty
    private String name;

    @Column(name="COLOR")
    @Getter
    @Setter
    @JsonProperty
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

    public Tag(UUID tagId, String name, String color, List<RequestTagProject> requestTagProjects,
               List<TagsToProject> tagsToProjects) {
        this.tagId = tagId;
        this.name = name;
        this.color = color;
        this.requestTagProjects = requestTagProjects;
        this.tagsToProjects = tagsToProjects;
    }

}
