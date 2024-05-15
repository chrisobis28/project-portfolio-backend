package com.team2a.ProjectPortfolio.Commons;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name="PROJECTS_TO_ACCOUNTS")
public class ProjectsToAccounts {

    @Id
    @Column(name="PTA_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private UUID ptaId;

    @Column(name="ROLE")
    @Getter
    @Setter
    private String role;

    public ProjectsToAccounts(UUID ptaId, String role) {
        this.ptaId = ptaId;
        this.role = role;
    }
}
