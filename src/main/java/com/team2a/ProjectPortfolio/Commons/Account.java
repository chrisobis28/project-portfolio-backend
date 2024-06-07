package com.team2a.ProjectPortfolio.Commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name="ACCOUNT")
@NoArgsConstructor
public class Account implements UserDetails {

    @Id
    @Column(name="USERNAME")
    @Getter
    @Setter
    @NotNull(message = "username can't be null")
    private String username;

    @Column(name = "NAME")
    @Getter
    @Setter
    @NotNull(message = "name can't be null")
    private String name;

    @Column(name= "PASSWORD")
    @Getter
    @Setter
    @NotNull(message = "password can't be null")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name="ROLE")
    @Getter
    @Setter
    private Role role;

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="ACCOUNT_USERNAME", updatable = false, insertable = false)
    private List<ProjectsToAccounts> projectsToAccounts = new ArrayList<>();

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="REQUEST_ACCOUNT")
    @JsonIgnore
    private List<Request> requests = new ArrayList<>();


    /**
     * Constructor for the Account class
     * @param username the username of the account
     * @param name the name of the account
     * @param password the password of the account
     * @param role the role of the account
     */
    public Account (String username, String name, String password, Role role) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    /**
     * Method to check if an account has a request for a project
     * @param projectID the id of the project
     * @return whether the account has a request for the project
     */
    public boolean hasRequestForProject (UUID projectID) {
        return requests.stream().anyMatch(request -> request.getProject().getProjectId().equals(projectID));
    }

    /**
     * Gets authorities for the account
     * @return the authorities for the account
     */
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities () {
        return List.of(new SimpleGrantedAuthority(role.toString()));
    }

    @Override
    public boolean isAccountNonExpired () {
        return true;
    }

    @Override
    public boolean isAccountNonLocked () {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired () {
        return true;
    }

    @Override
    public boolean isEnabled () {
        return true;
    }
}
