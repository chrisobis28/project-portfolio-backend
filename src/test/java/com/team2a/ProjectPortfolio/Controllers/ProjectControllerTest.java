package com.team2a.ProjectPortfolio.Controllers;

import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Services.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

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
}