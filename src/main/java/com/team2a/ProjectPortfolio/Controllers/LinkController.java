package com.team2a.ProjectPortfolio.Controllers;


import com.team2a.ProjectPortfolio.Commons.Link;

import com.team2a.ProjectPortfolio.Commons.RequestLinkProject;
import com.team2a.ProjectPortfolio.CustomExceptions.NotFoundException;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.LinkService;
import com.team2a.ProjectPortfolio.WebSocket.LinkProjectWebSocketHandler;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static com.team2a.ProjectPortfolio.security.Permissions.*;

@RestController
@RequestMapping(Routes.LINK)
@CrossOrigin("http://localhost:4200")
public class LinkController {
    private final LinkService linkService;

    private final LinkProjectWebSocketHandler linkProjectWebSocketHandler;

    /**
     * The constructor for the link controller
     * @param linkService the link service instance
     * @param linkProjectWebSocketHandler the web socket handler for links to projects
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
    @PreAuthorize(EDITOR_IN_PROJECT)
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
     * @param projectId
     * @return the new link entity
     */
    @PutMapping("/{projectId}")
    @PreAuthorize(EDITOR_IN_PROJECT)
    public ResponseEntity<Link> editLinkOfProject (@RequestBody Link link, @PathVariable("projectId") UUID projectId) {
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
    @GetMapping("/public/{projectId}")
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
     * @param projectId
     * @return a string containing a message if the link was deleted
     */
    @DeleteMapping("/{linkId}/{projectId}")
    @PreAuthorize(EDITOR_IN_PROJECT)
    public ResponseEntity<String> deleteLinkById (@PathVariable("linkId") UUID linkId,
                                                  @PathVariable("projectId") UUID projectId) {
        try {
            String returnedMessage = linkService.deleteLinkById(linkId);
            linkProjectWebSocketHandler.broadcast(returnedMessage);
            return ResponseEntity.ok(returnedMessage);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/request/{requestId}/{projectId}")
    @PreAuthorize(PM_IN_PROJECT)
    public ResponseEntity<List<RequestLinkProject>> getLinksForRequest (@PathVariable("requestId") UUID requestId,
                                                                        @PathVariable("projectId") UUID projectId) {
        try {
            List<RequestLinkProject> body = linkService.getLinksForRequest(requestId);
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/request/remove/{requestId}/{linkId}/{projectId}")
    @PreAuthorize(USER_IN_PROJECT)
    public ResponseEntity<Link> addRemovedLinkToRequest (@PathVariable("requestId") UUID requestId,
                                                         @PathVariable("linkId") UUID linkId,
                                                         @PathVariable("projectId") UUID projectId){
        try {
            Link body = linkService.addRemovedLinkToRequest(requestId, linkId);
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("request/add/{requestId}/{projectId}")
    @PreAuthorize(USER_IN_PROJECT)
    public ResponseEntity<Link> addAddedLinkToRequest (@PathVariable("requestId") UUID requestId,
                                                       @RequestBody Link link,
                                                       @PathVariable("projectId") UUID projectId) {
        try {
            Link body = linkService.addAddedLinkToRequest(requestId, link);
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
