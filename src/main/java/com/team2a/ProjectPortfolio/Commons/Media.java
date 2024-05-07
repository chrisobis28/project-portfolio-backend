package com.team2a.ProjectPortfolio.Commons;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name="MEDIA")
public class Media {
    @Id
    @Column(name="MEDIAID")
    private UUID mediaId;

    @Column(name="PATH")
    private String path;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name="PROJECTID", referencedColumnName = "PROJECTID")
    private Project project;
}
