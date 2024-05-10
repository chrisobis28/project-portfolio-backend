package com.team2a.ProjectPortfolio.Commons;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "LINK")
public class Link {
    @Id
    @Column(name="LINK_ID", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private UUID linkId;

    @Column(name="NAME")
    @Getter
    @Setter
    private String name;

    @Column(name="URL")
    @Getter
    @Setter
    private String url;

    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="LINK_ID")
    private List<RequestLinkProject> requestLinkProjects;

    public Link(UUID linkId, String name, String url, List<RequestLinkProject> requestLinkProjects) {
        this.linkId = linkId;
        this.name = name;
        this.url = url;
        this.requestLinkProjects = requestLinkProjects;
    }
}
