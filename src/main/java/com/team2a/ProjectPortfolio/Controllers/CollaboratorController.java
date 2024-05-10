package com.team2a.ProjectPortfolio.Controllers;

import com.team2a.ProjectPortfolio.Commons.Collaborator;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.CollaboratorService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Routes.COLLABORATORS)
public class CollaboratorController {
    private final transient CollaboratorService collaboratorService;

    /**
     * Constructor for the collaborator controller
     * @param collaboratorService the collaborator service
     */
    @Autowired
    public CollaboratorController(CollaboratorService collaboratorService) {
        this.collaboratorService = collaboratorService;
    }

    /**
     * Returns a list of Collaborators in a response body that are part of a certain project
     * @param projectId the project id
     * @return a response entity that contains the list of collaborators entities
     */
    @GetMapping("/{projectId}")
    public ResponseEntity<List<Collaborator>> getCollaboratorsByProjectId (@PathVariable("projectId") UUID projectId){
        try {
            List<Collaborator> collaboratorsList = collaboratorService.getCollaboratorsByProjectId(projectId);
            return ResponseEntity.ok(collaboratorsList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Adds a collaborator to a specified projectId. If the collaborator is already in the database, we just
     * link it to the project. Otherwise, we create it and link to the project.
     * @param projectId the projectId
     * @param collaboratorName the collaboratorName
     * @return a responseEntity containing a collaborator entity
     */
    @PostMapping("/{projectId}")
    public ResponseEntity<Collaborator> addCollaboratorToProject (@PathVariable("projectId") UUID projectId,
                                                                  @RequestBody String collaboratorName){
        try {
            Collaborator collaborator = collaboratorService.addCollaboratorToProject(projectId,collaboratorName);
            return ResponseEntity.ok(collaborator);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Changes the name of a collaborator
     * @param collaboratorId the collaborator id
     * @param collaboratorName the new collaborator name
     * @return a responseEntity containing a collaborator entity
     */
    @PutMapping("/{collaboratorId}")
    public ResponseEntity<Collaborator> editCollaboratorOfProject (@PathVariable("collaboratorId") UUID collaboratorId,
                                                                  @RequestBody String collaboratorName){
        try {
            Collaborator collaborator = collaboratorService.editCollaboratorOfProject(collaboratorId,collaboratorName);
            return ResponseEntity.ok(collaborator);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
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
    public ResponseEntity<String> deleteCollaborator (@PathVariable("collaboratorId") UUID collaboratorId){
        try {
            String response = collaboratorService.deleteCollaborator(collaboratorId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
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
    public ResponseEntity<String> deleteCollaboratorFromProject (@PathVariable("projectId") UUID projectId,
                                                                 @PathVariable("collaboratorId") UUID collaboratorId){
        try {
            String response = collaboratorService.deleteCollaboratorFromProject(projectId,collaboratorId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
