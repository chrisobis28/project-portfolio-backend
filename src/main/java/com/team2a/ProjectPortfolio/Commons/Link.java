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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Entity
@Table(name = "LINK")
@Data
public class Link {
    @Id
    @Column(name="LINK_ID", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    @JsonProperty
    private UUID linkId;

    @Column(name="NAME")
    @Getter
    @Setter
    @JsonProperty
    private String name;

    @Column(name="URL")
    @Getter
    @Setter
    @JsonProperty
    private String url;

    @ManyToOne
    @JoinColumn(name="PROJECT_ID")
    @Getter
    @Setter
    @JsonIgnore
    private Project project;

    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="LINK_ID")
    private List<RequestLinkProject> requestLinkProjects = new ArrayList<>();

    public Link(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
