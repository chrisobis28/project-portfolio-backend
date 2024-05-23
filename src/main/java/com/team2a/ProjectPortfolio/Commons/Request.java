package com.team2a.ProjectPortfolio.Commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="REQUEST")
@NoArgsConstructor
@EqualsAndHashCode
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
    @ManyToOne
    @JoinColumn(name="REQUEST_PROJECT")
    @NotNull (message = "Project must be specified")
    private Project project;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name="ACCOUNT_USERNAME", nullable=false)
    @NotNull (message = "Account must be specified")
    @JsonIgnore
    private Account account;



    @Getter
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="REQUEST_ID", updatable = false, insertable = false)
    @JsonIgnore
    private List<RequestTagProject> requestTagProjects = new ArrayList<>();

    @Getter
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="REQUEST_ID", updatable = false, insertable = false)
    @JsonIgnore
    private List<RequestMediaProject> requestMediaProjects = new ArrayList<>();

    @Getter
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="REQUEST_ID", updatable = false, insertable = false)
    @JsonIgnore
    private List<RequestLinkProject> requestLinkProjects = new ArrayList<>();


    @Getter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="REQUEST_ID", updatable = false, insertable = false)
    @JsonIgnore
    private List<RequestCollaboratorsProjects> requestCollaboratorsProjects = new ArrayList<>();

    /**
     * Constructor for the Request class
     * @param newTitle the new title set
     * @param newDescription the new description set
     * @param newBibtex the new Bibtex set
     * @param isCounterOffer whether the request is a counter offer
     * @param account the account associated with the request
     * @param project the project associated with the request
     */
    public Request(String newTitle, String newDescription, String newBibtex, boolean isCounterOffer,
                   Account account, Project project) {
        this.newTitle = newTitle;
        this.newDescription = newDescription;
        this.newBibtex = newBibtex;
        this.isCounterOffer = isCounterOffer;
        this.account = account;
        this.project = project;
    }

    /**
     * Setter for the LinksChanged field, given a list of Link elements
     * @param linksChanged the Links changed in the request
     */
    public void setLinksChanged (List<Link> linksChanged) {
        this.requestLinkProjects = linksChanged.stream().map(RequestLinkProject::new).toList();
    }

    /**
     * Setter for the collaboratorsChanged field, given a list of Collaborator elements
     * @param collaboratorsChanged the list of collaborators changed
     */
    public void setCollaboratorsChanged (List<Collaborator> collaboratorsChanged) {
        this.requestCollaboratorsProjects = collaboratorsChanged.stream().map(RequestCollaboratorsProjects::new).toList();
    }

    /**
     * Setter for the tagsChanged field, given a list of Tag elements
     * @param tagsChanged the tags changed in the request
     */
    public void setTagsChanged (List<Tag> tagsChanged) {
        this.requestTagProjects = tagsChanged.stream().map(RequestTagProject::new).toList();
    }

    /**
     * Setter for the mediaChanged field, given a list of Media elements
     * @param mediaChanged the Media elements changed
     */
    public void setMediaChanged (List<Media> mediaChanged) {
        this.requestMediaProjects = mediaChanged.stream().map(RequestMediaProject::new).toList();
    }


    /**
     * getter for the Media changed in the request, based on the mediaChanged field
     * @return the list of Media changed
     */
    public List<Media> getMedia () {
        if(requestMediaProjects.isEmpty())
            return new ArrayList<>();
        return requestMediaProjects.stream().map(RequestMediaProject::getMedia).toList();
    }

    /**
     * getter for the Tags changed in a request based on the tagsChanmged field
     * @return the list of Tag elements changed in the request
     */
    public List<Tag> getTags () {
        if(requestTagProjects.isEmpty())
            return new ArrayList<>();
        return  requestTagProjects.stream().map(RequestTagProject::getTag).toList();
    }

    /**
     * Getter for the list of Collaborators changed in the request
     * @return the list of Collaborators changed
     */
    public List<Collaborator> getCollaborators () {
        if(requestCollaboratorsProjects.isEmpty())
            return new ArrayList<>();
        return requestCollaboratorsProjects.stream().map(RequestCollaboratorsProjects::getCollaborator).toList();
    }

    /**
     * Getter for the list of Links changed in the request
     * @return the list of Links changed
     */
    public List<Link> getLinks () {
        if(requestLinkProjects.isEmpty())
            return new ArrayList<>();
        return requestLinkProjects.stream().map(RequestLinkProject::getLink).toList();
    }

}
