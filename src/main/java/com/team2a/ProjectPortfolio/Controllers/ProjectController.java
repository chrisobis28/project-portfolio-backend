package com.team2a.ProjectPortfolio.Controllers;

import static com.team2a.ProjectPortfolio.security.Permissions.EDITOR_IN_PROJECT;
import static com.team2a.ProjectPortfolio.security.Permissions.PM_IN_PROJECT;
import static com.team2a.ProjectPortfolio.security.Permissions.PM_ONLY;

import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Routes.PROJECT)
@CrossOrigin("http://localhost:4200")
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
     * Delete a project by its ID
     * @param projectId the id of the project to be deleted
     * @return a response entity containing a string
     */
    @DeleteMapping("/{projectId}")
    @PreAuthorize(PM_IN_PROJECT)
    public ResponseEntity<Void> deleteProject (@PathVariable("projectId") UUID projectId){
        projectService.deleteProject(projectId);
        return ResponseEntity.ok().build();
    }

    /**
     * Returns an updated project given an ID
     * @param projectId the id of a project
     * @param project the project updates that will be persisted in the DB
     * @return the changed project with the specified ID
     */
    @PutMapping("/{projectId}")
    @PreAuthorize(EDITOR_IN_PROJECT)
    public ResponseEntity<Project> updateProject (@PathVariable("projectId") UUID projectId,
                                                  @Valid @RequestBody Project project) {
        Project result = projectService.updateProject(projectId, project);
        return ResponseEntity.ok(result);
    }

    /**
     * Returns a Project queried by its ID
     * @param projectId the id of the project
     * @return a response entity that contains the project with the specified id
     */
    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getProjectById (@PathVariable("projectId") UUID projectId) {
        Project project = projectService.getProjectById(projectId);
        return ResponseEntity.ok(project);
    }

    /**
     * Creates a new project and returns it in a response entity
     * @param project A json deserialized object with the attributes for the project
     * @return a response entity that contains the added project
     */
    @PostMapping("/")
    @PreAuthorize(PM_ONLY)
    public ResponseEntity<Project> createProject (@Valid @RequestBody Project project) {
        Project response = projectService.createProject(project);
        return ResponseEntity.ok(response);
    }

}
