package com.team2a.ProjectPortfolio.Controllers;

import com.team2a.ProjectPortfolio.Commons.Collaborator;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.CollaboratorService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @return a response entity that contains the list of collaborators
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
}
