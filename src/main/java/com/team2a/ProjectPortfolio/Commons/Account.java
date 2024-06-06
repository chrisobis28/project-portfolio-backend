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

    @Column(name= "IS_ADMINISTRATOR")
    @Getter
    @Setter
    @NotNull(message = "isAdministrator can't be null")
    private Boolean isAdministrator;

    @Column(name= "IS_PM")
    @Getter
    @Setter
    @NotNull(message = "isPM can't be null")
    private Boolean isPM;

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
     * @param isAdministrator whether the account is an administrator
     * @param isPM whether the account is a project manager
     */
    public Account (String username, String name, String password, Boolean isAdministrator,
                   Boolean isPM) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.isAdministrator = isAdministrator;
        this.isPM = isPM;
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
        if(isAdministrator) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        if(isPM) {
            return List.of(new SimpleGrantedAuthority("ROLE_PM"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
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
