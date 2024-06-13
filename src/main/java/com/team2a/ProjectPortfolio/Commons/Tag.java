package com.team2a.ProjectPortfolio.Commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
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
    @NotNull (message = "Name must be specified")
    private String name;

    @Column(name="COLOR")
    @Getter
    @Setter
    @JsonProperty
    @NotNull (message = "Color must be specified")
    private String color;


    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action=OnDeleteAction.CASCADE)
    @JoinColumn(name="TAG_ID", updatable = false, insertable = false)
    @JsonIgnore
    private List<RequestTagProject> requestTagProjects = new ArrayList<>();

    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action=OnDeleteAction.CASCADE)
    @JoinColumn(name="TAG_ID", updatable = false, insertable = false)
    private List<TagsToProject> tagsToProjects = new ArrayList<>();

    public Tag(String name, String color) {
        this.name = name;
        this.color = color;
    }

}
