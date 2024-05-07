package com.team2a.ProjectPortfolio.Commons;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "REQUESTMEDIAPROJECT")
public class RequestMediaProject {
    @Id
    @Column(name="REQUESTMEDIAPROJECT_ID", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private UUID requestMediaProjectId;

    @Column(name="IS_REMOVE")
    @Getter
    @Setter
    private boolean isRemove;
}
