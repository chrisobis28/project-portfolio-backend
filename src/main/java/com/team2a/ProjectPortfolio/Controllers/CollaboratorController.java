package com.team2a.ProjectPortfolio.Controllers;

import static com.team2a.ProjectPortfolio.Routes.hostLink;
import static com.team2a.ProjectPortfolio.security.Permissions.PM_IN_PROJECT;
import static com.team2a.ProjectPortfolio.security.Permissions.PM_ONLY;

import com.team2a.ProjectPortfolio.Commons.Collaborator;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.CollaboratorService;
import com.team2a.ProjectPortfolio.WebSocket.CollaboratorProjectWebSocketHandler;
import com.team2a.ProjectPortfolio.WebSocket.CollaboratorWebSocketHandler;
import com.team2a.ProjectPortfolio.dto.CollaboratorTransfer;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Routes.COLLABORATOR)
@CrossOrigin(hostLink)
public class CollaboratorController {
    private final CollaboratorService collaboratorService;

    private final CollaboratorWebSocketHandler collaboratorWebSocketHandler;

    private final CollaboratorProjectWebSocketHandler collaboratorProjectWebSocketHandler;

    /**
     * Constructor for the collaborator controller
     * @param collaboratorService the collaborator service instance
     * @param collaboratorWebSocketHandler the web socket handler used for collaborators
     * @param collaboratorProjectWebSocketHandler the web socket handler used for collaborators for projects
     */
    @Autowired
    public CollaboratorController(CollaboratorService collaboratorService,
                                  CollaboratorWebSocketHandler collaboratorWebSocketHandler,
                                  CollaboratorProjectWebSocketHandler collaboratorProjectWebSocketHandler) {
        this.collaboratorService = collaboratorService;
        this.collaboratorWebSocketHandler = collaboratorWebSocketHandler;
        this.collaboratorProjectWebSocketHandler = collaboratorProjectWebSocketHandler;
    }

    /**
     * Returns a list of Collaborators in a response body that are part of a certain project
     * @param projectId the project id
     * @return a response entity that contains the list of collaborators entities
     */
    @GetMapping("/public/{projectId}")
    public ResponseEntity<List<CollaboratorTransfer>> getCollaboratorsByProjectId
    (@PathVariable("projectId") UUID projectId){
        return ResponseEntity.ok(collaboratorService.getCollaboratorsByProjectId(projectId));
    }

    /**
     * Adds a collaborator to the database. If it already exists a collaborator
     * with the same name, returns that one
     * @param name the name of the collaborator to be added
     * @return the collaborator with the specified name
     */
    @PutMapping("/")
    @PreAuthorize(PM_ONLY)
    public ResponseEntity<Collaborator> addCollaborator
    (@RequestBody String name) {
        Collaborator c = collaboratorService.addCollaborator(name);
        collaboratorWebSocketHandler.broadcast("Collaborator added");
        return new ResponseEntity<>(c, HttpStatus.OK);
    }

    /**
     * Changes the name of a collaborator
     * @param collaboratorId the collaborator id
     * @param collaboratorName the new collaborator name
     * @return a responseEntity containing a collaborator entity
     */
    @PutMapping("/{collaboratorId}")
    @PreAuthorize(PM_ONLY)
    public ResponseEntity<Collaborator> editCollaboratorOfProject (@PathVariable("collaboratorId") UUID collaboratorId,
                                                                  @RequestBody String collaboratorName){
        try {
            Collaborator collaborator = collaboratorService.editCollaboratorOfProject(collaboratorId,collaboratorName);
            collaboratorWebSocketHandler.broadcast("Collaborator Changed");
            collaboratorProjectWebSocketHandler.broadcast("all");
            return ResponseEntity.ok(collaborator);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a collaborator by his id
     * @param collaboratorId the collaborator ID
     * @return a responseEntity containing an error or a string
     */
    @DeleteMapping("/{collaboratorId}")
    @PreAuthorize(PM_ONLY)
    public ResponseEntity<String> deleteCollaborator (@PathVariable("collaboratorId") UUID collaboratorId){
        try {
            String response = collaboratorService.deleteCollaborator(collaboratorId);
            collaboratorWebSocketHandler.broadcast("deleted " + collaboratorId.toString());
            collaboratorProjectWebSocketHandler.broadcast("all");
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a collaborator from a project based on his collaborator id and the project id
     * @param projectId the project ID
     * @param collaboratorId the collaborator ID
     * @return a responseEntity containing an error or a string
     */
    @DeleteMapping("/{projectId}/{collaboratorId}")
    @PreAuthorize(PM_IN_PROJECT)
    public ResponseEntity<String> deleteCollaboratorFromProject (@PathVariable("projectId") UUID projectId,
                                                                 @PathVariable("collaboratorId") UUID collaboratorId){
        try {
            String response = collaboratorService.deleteCollaboratorFromProject(projectId,collaboratorId);
            collaboratorProjectWebSocketHandler.broadcast(projectId.toString());
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * endpoint for retrieving all collaborators
     * @return a list of collaborators
     */
    @GetMapping("/public/")
    public ResponseEntity<List<Collaborator>> getAllCollaborators () {
        List<Collaborator> collaborators = collaboratorService.getAllCollaborators();
        return new ResponseEntity<>(collaborators, HttpStatus.OK);
    }

    /**
     * endpoint for creating and adding a collaborator to a project
     * @param collaborator the collaborator to be added
     * @param projectId the project ID
     * @return a response entity containing the collaborator entity
     */
    @PostMapping("/{projectId}")
    @PreAuthorize(PM_IN_PROJECT)
    public ResponseEntity<CollaboratorTransfer>
        createAndAddCollaboratorToProject (@RequestBody CollaboratorTransfer collaborator,
                                       @PathVariable("projectId") UUID projectId) {
        collaboratorProjectWebSocketHandler.broadcast("all");
        return ResponseEntity.ok(collaboratorService.createAndAddCollaboratorToProject(projectId, collaborator));
    }
}
