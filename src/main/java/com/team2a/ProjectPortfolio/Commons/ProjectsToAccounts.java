package com.team2a.ProjectPortfolio.Commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

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

    @Column(name="ROLE")
    @Getter
    @Setter
    @NotNull(message = "role can't be null")
    private String role;

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

    public ProjectsToAccounts(String role, Account account, Project project) {
        this.role = role;
        this.account = account;
        this.project = project;
    }
}
