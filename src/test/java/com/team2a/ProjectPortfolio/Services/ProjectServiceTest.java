package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectServiceTest {

    private ProjectRepository projectRepository;
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectRepository = mock(ProjectRepository.class);
        projectService = new ProjectService(projectRepository);
    }

    @Test
    void getProjectsEmpty() {
        List<Project> expected = new ArrayList<>();
        when(projectRepository.findAll()).thenReturn(List.of());
        List<Project> response = projectService.getProjects();
        assertEquals(expected, response);
    }

    @Test
    void getProjectsNotEmpty() {
        Project project1 = new Project("Title1", "Description1", "Bibtex1", false);
        Project project2 = new Project("Title2", "Description2", "Bibtex2", false);
        Project project3 = new Project("Title3", "Description3", "Bibtex3", false);
        List<Project> projects = List.of(project1, project2, project3);

        when(projectRepository.findAll()).thenReturn(projects);

        List<Project> response = projectService.getProjects();
        assertEquals(projects, response);
    }

    @Test
    void updateProjectSuccess() {
        UUID projectId = UUID.randomUUID();
        Project project1 = new Project("Title1", "Description1", "Bibtex1", false);
        Project project2 = new Project("Title2", "Description2", "Bibtex2", false);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project1));
        when(projectRepository.save(project1)).thenReturn(project2);
        Project response = projectService.updateProject(projectId, project2);
        assertEquals(project2, response);
    }

    @Test
    void updateProjectNullId() {
        Project project1 = new Project("Title1", "Description1", "Bibtex1", false);
        assertThrows(IllegalArgumentException.class, () -> projectService.updateProject(null, project1));
    }

    @Test
    void updateProjectNotFound() {
        UUID projectId = UUID.randomUUID();
        Project project1 = new Project("Title1", "Description1", "Bibtex1", false);
        assertThrows(EntityNotFoundException.class, () -> projectService.updateProject(projectId, project1));
    }
    @Test
    void createProjectSuccess() {
        String title = "title1";
        String desc = "desc1";
        String bibtex = "bibtex1";
        Project project = new Project(title, desc, bibtex, false);
        when(projectRepository.findFirstByTitleAndDescriptionAndBibtex(title, desc, bibtex))
                .thenReturn(Optional.empty());
        when(projectRepository.save(any())).thenReturn(new Project(title, desc, bibtex, false));
        Project response = projectService.createProject(project);
        assertEquals(project.getTitle(), response.getTitle());
        assertEquals(project.getDescription(), response.getDescription());
        assertEquals(project.getBibtex(), response.getBibtex());
    }

    @Test
    void createProjectNull() {
        assertThrows(IllegalArgumentException.class, () -> projectService.createProject(null));
    }

    @Test
    void createProjectExistsAlready() {
        String title = "title1";
        String desc = "desc1";
        String bibtex = "bibtex1";
        Project project = new Project(title, desc, bibtex, false);
        when(projectRepository.findFirstByTitleAndDescriptionAndBibtex(title, desc, bibtex))
                .thenReturn(Optional.of(new Project(title, desc, bibtex, false)));
        Project response = projectService.createProject(project);
        assertEquals(project.getTitle(), response.getTitle());
        assertEquals(project.getDescription(), response.getDescription());
        assertEquals(project.getBibtex(), response.getBibtex());
    }

    @Test
    void getProjectByIdSuccess() {
        UUID projectId = UUID.randomUUID();
        Project project1 = new Project("Title1", "Description1", "Bibtex1", false);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project1));
        Project response = projectService.getProjectById(projectId);
        assertEquals(project1, response);
    }

    @Test
    void getProjectByIdNull() {
        assertThrows(IllegalArgumentException.class, () -> projectService.getProjectById(null));
    }

    @Test
    void getProjectByIdNotFound() {
        UUID projectId = UUID.randomUUID();
        assertThrows(EntityNotFoundException.class, () -> projectService.getProjectById(projectId));
    }

}