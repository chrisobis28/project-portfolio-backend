package com.team2a.ProjectPortfolio.Commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="PROJECTS_TO_ACCOUNTS")
@NoArgsConstructor
public class ProjectsToAccounts {

    @Id
    @Column(name="PTA_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private UUID ptaId;

    @Enumerated(EnumType.STRING)
    @Column(name="ROLE")
    @Getter
    @Setter
    private RoleInProject role;

    @ManyToOne
    @JoinColumn(name="ACCOUNT_USERNAME")
    @Getter
    @Setter
    @JsonIgnore
    private Account account;

    @ManyToOne
    @JoinColumn(name="PROJECT_ID")
    @Getter
    @Setter
    @JsonIgnore
    private Project project;

    public ProjectsToAccounts(RoleInProject role, Account account, Project project) {
        this.role = role;
        this.account = account;
        this.project = project;
    }
}
