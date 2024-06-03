package com.team2a.ProjectPortfolio.Controllers;


import com.team2a.ProjectPortfolio.Commons.Link;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.LinkService;
import com.team2a.ProjectPortfolio.WebSocket.LinkProjectWebSocketHandler;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Routes.LINK)
@CrossOrigin("http://localhost:4200")
public class LinkController {
    private final LinkService linkService;

    private final LinkProjectWebSocketHandler linkProjectWebSocketHandler;

    /**
     * The constructor for the LinkController
     * @param linkService the Link Service
     */
    @Autowired
    public LinkController(LinkService linkService,
                          LinkProjectWebSocketHandler linkProjectWebSocketHandler) {
        this.linkService = linkService;
        this.linkProjectWebSocketHandler = linkProjectWebSocketHandler;
    }

    /**
     * Add a link to the project
     * @param link the link entity
     * @param projectId the project ID
     * @return the new link entity
     */
    @PostMapping("/{projectId}")
    public ResponseEntity<Link> addLinkToProject (@RequestBody Link link,@PathVariable("projectId") UUID projectId) {
        try {
            Link newLink = linkService.addLinkToProject(link,projectId);
            linkProjectWebSocketHandler.broadcast(projectId.toString());
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
    @PutMapping("/")
    public ResponseEntity<Link> editLinkOfProject (@RequestBody Link link) {
        try {
            Link updatedLink = linkService.editLinkOfProject(link);
            linkProjectWebSocketHandler.broadcast(updatedLink.getProject().getProjectId().toString());
            return ResponseEntity.ok(updatedLink);
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
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a link based on its id
     * @param linkId the linkId of the link to be deleted
     * @return a string containing a message if the link was deleted
     */
    @DeleteMapping("/{linkId}")
    public ResponseEntity<String> deleteLinkById (@PathVariable("linkId") UUID linkId) {
        try {
            String returnedMessage = linkService.deleteLinkById(linkId);
            linkProjectWebSocketHandler.broadcast(returnedMessage);
            return ResponseEntity.ok(returnedMessage);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
