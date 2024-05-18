package com.team2a.ProjectPortfolio.Commons;

import jakarta.persistence.*;
import java.util.ArrayList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Table(name="ACCOUNT")
@NoArgsConstructor
public class Account {

    @Id
    @Column(name="USERNAME")
    @Getter
    @Setter
    private String username;

    @Column(name = "NAME")
    @Getter
    @Setter
    private String name;

    @Column(name= "PASSWORD")
    @Getter
    @Setter
    private String password;

    @Column(name= "IS_ADMINISTRATOR")
    @Getter
    @Setter
    private Boolean isAdministrator;

    @Column(name= "IS_PM")
    @Getter
    @Setter
    private Boolean isPM;

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="ACCOUNT_USERNAME")
    private List<ProjectsToAccounts> projectsToAccounts = new ArrayList<>();

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="REQUEST_ACCOUNT")
    private List<Request> requests = new ArrayList<>();

    public Account(String username, String name, String password, Boolean isAdministrator,
                   Boolean isPM) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.isAdministrator = isAdministrator;
        this.isPM = isPM;
    }
}
