package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Link;
import com.team2a.ProjectPortfolio.Repositories.LinkRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class LinkService {
    private final LinkRepository linkRepository;

    /**
     * Constructor for the link repository
     * @param linkRepository the Link repository
     */
    @Autowired
    public LinkService (LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    /**
     * Edit the link of the project
     * @param link the link entity
     * @return the new link entity
     */
    public Link editLinkOfProject (Link link) {
        if(link == null) {
            throw new IllegalArgumentException();
        }
        if(linkRepository.findById(link.getLinkId()).isPresent()) {
            linkRepository.save (link);
        }
        else
            throw new EntityNotFoundException();
        return link;
    }

}
