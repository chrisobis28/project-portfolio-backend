package com.team2a.ProjectPortfolio.Controllers;


import com.team2a.ProjectPortfolio.Commons.Link;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.LinkService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Routes.LINK)
public class LinkController {
    private final LinkService linkService;

    /**
     * The constructor for the LinkController
     * @param linkService the Link Service
     */
    @Autowired
    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    /**
     * Add a link to the project
     * @param link the link entity
     * @return the new link entity
     */
    public ResponseEntity<Link> addLinkToProject (@RequestBody Link link) {
        try {
            Link newLink = linkService.addLinkToProject(link);
            return ResponseEntity.ok(newLink);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
    }

    /**
     * Edit the link of the project
     * @param link the link entity
     * @return the new link entity
     */
    @PutMapping("/edit")
    public ResponseEntity<Link> editLinkOfProject (@RequestBody Link link) {
        try {
            Link updatedLink = linkService.editLinkOfProject(link);
            return ResponseEntity.ok(updatedLink);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get the links of a project given its ID
     * @param projectId the id of the project
     * @return the links associated with a project given the id of the project
     */
    @GetMapping("/{projectId}")
    public ResponseEntity<List<Link>> getLinksByProjectId (@PathVariable("projectId") UUID projectId) {
        try {
            List<Link> links = linkService.getLinksByProjectId(projectId);
            return ResponseEntity.ok(links);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
