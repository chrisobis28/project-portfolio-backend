package com.team2a.ProjectPortfolio.Commons;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="REQUEST")
@NoArgsConstructor
@ToString
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
    private Project project;

    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="REQUEST_ID")
    private List<RequestTagProject> requestTagProjects;

    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="REQUEST_ID")
    private List<RequestMediaProject> requestMediaProjects;

    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="REQUEST_ID")
    private List<RequestLinkProject> requestLinkProjects;


    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @JoinColumn(name="REQUEST_ID")
    private List<RequestCollaboratorsProjects> requestCollaboratorsProjects;


    public Request (String newTitle, String newDescription, String newBibtex, Boolean isCounterOffer,
                    Object[] linksChanged, Object[] mediaChanged, Object[] tagsChanged, Object[] collaboratorsChanged) {

        Logger logger = LoggerFactory.getLogger(Request.class);
        logger.debug(Arrays.toString(mediaChanged));
        this.newTitle = newTitle;
        this.newDescription = newDescription;
        this.newBibtex = newBibtex;
        this.isCounterOffer = isCounterOffer;
        this.requestLinkProjects = Arrays.stream(linksChanged).map(x -> new RequestLinkProject((Link) x, this)).toList();
        this.requestCollaboratorsProjects = Arrays.stream(collaboratorsChanged).map(x -> new RequestCollaboratorsProjects(this, (Collaborator) x)).toList();
        this.requestTagProjects = Arrays.stream(tagsChanged).map(x -> new RequestTagProject(this, (Tag) x)).toList();
        this.requestMediaProjects = Arrays.stream(mediaChanged).map(x -> new RequestMediaProject(this, (Media) x)).toList();
    }

    public Request (UUID requestId, String newTitle, String newDescription, String newBibtex, Boolean isCounterOffer) {
        this.requestId = requestId;
        this.newTitle = newTitle;
        this.newDescription = newDescription;
        this.newBibtex = newBibtex;
        this.isCounterOffer = isCounterOffer;
        this.requestMediaProjects = new ArrayList<>();
        this.requestTagProjects = new ArrayList<>();
        this.requestLinkProjects = new ArrayList<>();
        this.requestCollaboratorsProjects = new ArrayList<>();
    }


    public List<Media> getMedia () {
        if(requestMediaProjects.isEmpty())
            return new ArrayList<>();
        return requestMediaProjects.stream().map(RequestMediaProject::getMedia).toList();
    }

    public List<Tag> getTags () {
        if(requestTagProjects.isEmpty())
            return new ArrayList<>();
        return  requestTagProjects.stream().map(RequestTagProject::getTag).toList();
    }

    public List<Collaborator> getCollaborators () {
        if(requestCollaboratorsProjects.isEmpty())
            return new ArrayList<>();
        return requestCollaboratorsProjects.stream().map(RequestCollaboratorsProjects::getCollaborator).toList();
    }

    public List<Link> getLinks () {
        if(requestLinkProjects.isEmpty())
            return new ArrayList<>();
        return requestLinkProjects.stream().map(RequestLinkProject::getLink).toList();
    }

}
