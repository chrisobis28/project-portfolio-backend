package com.team2a.ProjectPortfolio.Commons;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name="MEDIA")
public class Media {
    @Id
    @Column(name="MEDIA_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private UUID mediaId;

    @Column(name="PATH")
    @Getter
    @Setter
    private String path;

    public Media(UUID mediaId, String path) {
        this.mediaId = mediaId;
        this.path = path;
    }
}
