package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Link;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Repositories.LinkRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class LinkService {

    private final ProjectRepository projectRepository;
    private final LinkRepository linkRepository;

    /**
     * Constructor for the link repository
     * @param linkRepository the Link repository
     * @param projectRepository the Project repository
     */
    @Autowired
    public LinkService(LinkRepository linkRepository, ProjectRepository projectRepository) {
        this.linkRepository = linkRepository;
        this.projectRepository = projectRepository;
    }

    /**
     * Add a link to the project
     * @param link the link entity
     * @param projectId the project ID
     * @return the new link entity
     */
    public Link addLinkToProject (Link link,UUID projectId) {
        if(!projectRepository.existsById(projectId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }
        if(linkRepository.existsByProjectProjectIdAndUrl(projectId, link.getUrl())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Link already exists");
        }
        Project project = projectRepository.findById(projectId).orElseThrow(EntityNotFoundException::new);
        link.setProject(project);
        return linkRepository.saveAndFlush(link);
    }

    /**
     * Edit the link of the project
     * @param link the link entity
     * @return the new link entity
     */
    public Link editLinkOfProject (Link link) {
        if(linkRepository.findById(link.getLinkId()).isPresent()) {
            linkRepository.save (link);
        }
        else
            throw new EntityNotFoundException();
        return link;
    }

    /**
     * Gets all the links associated to a project given its id
     * @param projectId the id of the project
     * @return a list of links associated with the project
     */
    public List<Link> getLinksByProjectId (UUID projectId) {
        List<Link> links = linkRepository.findAllByProjectProjectId(projectId);
        return links;
    }

    /**
     * Delete link by its Id
     * @param linkId the linkId
     * @return a string if the link is deleted
     */
    public String deleteLinkById (UUID linkId){
        linkRepository.findById(linkId).orElseThrow(EntityNotFoundException::new);
        linkRepository.deleteById(linkId);
        return "Deleted link";
    }


}
