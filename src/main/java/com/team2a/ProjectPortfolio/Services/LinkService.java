package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Link;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.Request;
import com.team2a.ProjectPortfolio.Commons.RequestLinkProject;
import com.team2a.ProjectPortfolio.CustomExceptions.NotFoundException;
import com.team2a.ProjectPortfolio.Repositories.LinkRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.RequestLinkProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.RequestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LinkService {

    private final ProjectRepository projectRepository;
    private final LinkRepository linkRepository;
    private final RequestLinkProjectRepository requestLinkProjectRepository;
    private final RequestRepository requestRepository;

    /**
     * Constructor for the link repository
     * @param linkRepository the Link repository
     * @param projectRepository the Project repository
     */
    @Autowired
    public LinkService(LinkRepository linkRepository, ProjectRepository projectRepository,
                       RequestLinkProjectRepository requestLinkProjectRepository, RequestRepository requestRepository) {
        this.linkRepository = linkRepository;
        this.projectRepository = projectRepository;
        this.requestLinkProjectRepository = requestLinkProjectRepository;
        this.requestRepository = requestRepository;
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
        Optional<Link> linkFound = linkRepository.findById(link.getLinkId());
        if(linkFound.isPresent()) {
            link.setProject(linkFound.get().getProject());
            linkRepository.save(link);
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
        Link l = linkRepository.findById(linkId).orElseThrow(EntityNotFoundException::new);
        linkRepository.deleteById(linkId);
        return l.getProject().getProjectId().toString();
    }

    public Link addRemovedLinkToRequest (UUID requestId, UUID linkId) {
        Link link = linkRepository.findById(linkId).orElseThrow(EntityNotFoundException::new);
        Request request = requestRepository.findById(requestId).orElseThrow(EntityNotFoundException::new);

        RequestLinkProject body = new RequestLinkProject(request, link, true);

        requestLinkProjectRepository.save(body);
        return link;
    }

    public Link addAddedLinkToRequest (UUID requestId, Link link) {
        Request req = requestRepository.findById(requestId).orElseThrow(NotFoundException::new);
        RequestLinkProject body = new RequestLinkProject(req, link, false);
        linkRepository.save(link);
        requestLinkProjectRepository.save(body);
        return link;
    }

    public List<RequestLinkProject> getLinksForRequest (UUID requestId) {
        Request req = requestRepository.findById(requestId).orElseThrow(EntityNotFoundException::new);
        return req.getRequestLinkProjects();
    }


}
