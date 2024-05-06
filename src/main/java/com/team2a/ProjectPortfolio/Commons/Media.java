package com.team2a.ProjectPortfolio.Commons;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Entity
@Table(name="MEDIA")
public class Media {
    @Id
    @Column(name="MEDIAID")
    private UUID mediaId;

    @Column(name="PATH")
    private String path;

    @ManyToOne
    @JoinColumn(name="PROJECTID", referencedColumnName = "PROJECTID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Project project;
}
