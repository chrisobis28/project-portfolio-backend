package com.team2a.ProjectPortfolio.Commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@NoArgsConstructor
@Table(name="MEDIA")
@Data
public class Media {
    @Id
    @Column(name="MEDIA_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    @JsonProperty
    private UUID mediaId;

    @Column(name="NAME")
    @Getter
    @Setter
    @JsonProperty
    private String name;

    @Column(name="PATH")
    @Getter
    @Setter
    @JsonProperty
    private String path;

    @Getter
    @Setter
    @ManyToOne
    @OnDelete(action=OnDeleteAction.CASCADE)
    @JoinColumn(name="PROJECT_ID")
    @JsonIgnore
    private Project project;

    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="MEDIA_ID")
    private List<RequestMediaProject> requestMediaProjects;

    public Media(Project project, String name, String path) {
        this.project = project;
        this.name = name;
        this.path = path;
    }
}
