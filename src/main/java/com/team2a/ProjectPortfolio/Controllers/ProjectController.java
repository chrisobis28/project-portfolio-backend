package com.team2a.ProjectPortfolio.Controllers;

import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
     * Returns a list of all Projects in a response entity
     * @return a response entity that contains the list of all projects
     */
    @GetMapping("/")
    public ResponseEntity<List<Project>> getProjects () {
        List<Project> projects = projectService.getProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * Creates a new project and returns it in a response entity
     * @param project A json deserialized object with the attributes for the project
     * @return a response entity that contains the added project
     */
    @PostMapping("/")
    public ResponseEntity<Project> createProject (@RequestBody Project project) {
        try {
            Project response = projectService.createProject(project);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
