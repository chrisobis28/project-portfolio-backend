package com.team2a.ProjectPortfolio.Commons;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "REQUEST_LINK_PROJECT")
public class RequestLinkProject {
    @Id
    @Column(name="REQUEST_LINK_PROJECT_ID", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private UUID requestLinkProjectId;

    @Column(name="IS_REMOVE")
    @Getter
    @Setter
    private boolean isRemove;

    public RequestLinkProject(UUID requestLinkProjectId, boolean isRemove) {
        this.requestLinkProjectId = requestLinkProjectId;
        this.isRemove = isRemove;
    }
}
