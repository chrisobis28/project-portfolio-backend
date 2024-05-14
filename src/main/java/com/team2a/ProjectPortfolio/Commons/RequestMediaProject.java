package com.team2a.ProjectPortfolio.Commons;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "REQUEST_MEDIA_PROJECT")
@ToString
public class RequestMediaProject {
    @Id
    @Column(name="REQUEST_MEDIA_PROJECT_ID", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private UUID requestMediaProjectId;

    @Column(name="IS_REMOVE")
    @Getter
    @Setter
    private boolean isRemove;


    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "REQUEST_ID")
    private Request request;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "MEDIA_ID")
    private Media media;

    public RequestMediaProject(UUID requestMediaProjectId, boolean isRemove) {
        this.requestMediaProjectId = requestMediaProjectId;
        this.isRemove = isRemove;
    }

    public RequestMediaProject(Request request, Media media) {
        this.request = request;
        this.media = media;
    }
}
