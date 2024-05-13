package com.team2a.ProjectPortfolio.Controllers;

import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Routes.PROJECT)
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Constructor for the project controller
     * @param projectService the project service
     */
    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Returns a list of all Projects in a response body
     * @return a response entity that contains the list of all projects
     */
    @GetMapping("/")
    public ResponseEntity<List<Project>> getProjects () {
        List<Project> projects = projectService.getProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * Returns a an updated project given an ID
     * @param projectId the id of a project
     * @param project the project updates that will be persisted in the DB
     * @return the changed project with the specified ID
     */
    @PutMapping("/{projectId}")
    public ResponseEntity<Project> updateProject (@PathVariable("projectId") UUID projectId,
                                                  @RequestBody Project project) {
        try {
            Project result = projectService.updateProject(projectId, project);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
