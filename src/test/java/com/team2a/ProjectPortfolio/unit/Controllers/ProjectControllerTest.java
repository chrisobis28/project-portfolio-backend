package com.team2a.ProjectPortfolio.unit.Controllers;

import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Controllers.ProjectController;
import com.team2a.ProjectPortfolio.Services.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectControllerTest {

    private ProjectService projectService;
    private ProjectController projectController;

    @BeforeEach
    void setUp() {
        projectService = mock(ProjectService.class);
        projectController = new ProjectController(projectService);
    }

    @Test
    void getProjectsEmpty() {
        List<Project> expected = new ArrayList<>();
        when(projectService.getProjects()).thenReturn(List.of());
        ResponseEntity<List<Project>> response = projectController.getProjects();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
    }

    @Test
    void getProjectsNotEmpty() {
        Project project1 = new Project("Title1", "Description1", "Bibtex1", false);
        Project project2 = new Project("Title2", "Description2", "Bibtex2", false);
        Project project3 = new Project("Title3", "Description3", "Bibtex3", false);
        List<Project> projects = List.of(project1, project2, project3);

        when(projectService.getProjects()).thenReturn(projects);

        ResponseEntity<List<Project>> response = projectController.getProjects();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projects, response.getBody());
    }

    @Test
    void updateProjectSuccess() {
        UUID projectId = UUID.randomUUID();
        Project project1 = new Project("Title1", "Description1", "Bibtex1", false);
        when(projectService.updateProject(projectId, project1)).thenReturn(project1);
        ResponseEntity<Project> response = projectController.updateProject(projectId, project1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(project1, response.getBody());
    }

    @Test
    void updateProjectNullId() {
        Project project1 = new Project("Title1", "Description1", "Bibtex1", false);
        when(projectService.updateProject(null, project1)).thenThrow(IllegalArgumentException.class);
        ResponseEntity<Project> response = projectController.updateProject(null, project1);
        assertNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateProjectNotFound() {
        UUID projectId = UUID.randomUUID();
        Project project1 = new Project("Title1", "Description1", "Bibtex1", false);
        when(projectService.updateProject(projectId, project1)).thenThrow(EntityNotFoundException.class);
        ResponseEntity<Project> response = projectController.updateProject(projectId, project1);
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createProjectNull() {
        when(projectService.createProject(null)).thenThrow(IllegalArgumentException.class);
        ResponseEntity<Project> response = projectController.createProject(null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createProjectSuccess() {
        Project project = new Project("title1", "desc1", "bibtex1", false);
        when(projectService.createProject(project)).thenReturn(project);
        ResponseEntity<Project> response = projectController.createProject(project);
        assertEquals(project, response.getBody());
    }

    @Test
    void getProjectByIdSuccess() {
        UUID projectId = UUID.randomUUID();
        Project project1 = new Project("Title1", "Description1", "Bibtex1", false);
        when(projectService.getProjectById(projectId)).thenReturn(project1);
        ResponseEntity<Project> response = projectController.getProjectById(projectId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(project1, response.getBody());
    }

    @Test
    void getProjectByIdNull() {
        when(projectService.getProjectById(null)).thenThrow(IllegalArgumentException.class);
        ResponseEntity<Project> response = projectController.getProjectById(null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getProjectByIdNotFound() {
        UUID projectId = UUID.randomUUID();
        when(projectService.getProjectById(projectId)).thenThrow(EntityNotFoundException.class);
        ResponseEntity<Project> response = projectController.getProjectById(projectId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}