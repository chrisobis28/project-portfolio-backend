package com.team2a.ProjectPortfolio.Commons;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "REQUEST_TAG_PROJECT")
public class RequestTagProject {
    @Id
    @Column(name="REQUEST_TAG_PROJECT_ID", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private UUID requestTagProjectID;

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
    @JoinColumn(name = "TAG_ID")
    private Tag tag;


    public RequestTagProject(UUID requestTagProjectID, boolean isRemove) {
        this.requestTagProjectID = requestTagProjectID;
        this.isRemove = isRemove;
    }

    public RequestTagProject(Request request, Tag tag) {
        this.request = request;
        this.tag = tag;
    }
}
