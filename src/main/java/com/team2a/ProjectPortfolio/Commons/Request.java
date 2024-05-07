package com.team2a.ProjectPortfolio.Commons;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name="REQUEST")
public class Request {

    @Id
    @Column(name="REQUEST_ID", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private UUID requestId;

    @Column(name="NEW_TITLE")
    @Nullable
    @Getter
    @Setter
    private String newTitle;

    @Column(name="NEW_DESCRIPTION")
    @Nullable
    @Getter
    @Setter
    private String newDescription;

    @Column(name="NEW_BIBTEX")
    @Nullable
    @Getter
    @Setter
    private String newBibtex;

    @Column(name="IS_COUNTEROFFER")
    @Getter
    @Setter
    private boolean isCounterOffer;

    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="REQUEST_ID")
    private List<RequestTagProject> requestTagProjects;

    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="REQUEST_ID")
    private List<RequestMediaProject> requestMediaProjects;

    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="REQUEST_ID")
    private List<RequestLinkProject> requestLinkProjects;


    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="REQUEST_ID")
    private List<RequestCollaboratorsProjects> requestCollaboratorsProjects;

    public Request(UUID requestId, String newTitle, String newDescription, String newBibtex, Boolean isCounterOffer) {
        this.requestId = requestId;
        this.newTitle = newTitle;
        this.newDescription = newDescription;
        this.newBibtex = newBibtex;
        this.isCounterOffer = isCounterOffer;
    }

}
