package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.ProjectsToAccounts;
import com.team2a.ProjectPortfolio.Commons.RoleInProject;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectsToAccountsRepository;
import com.team2a.ProjectPortfolio.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceTest {
    private ProjectRepository projectRepository;

    private ProjectsToAccountsRepository projectsToAccountsRepository;
    private SecurityUtils securityUtils;
    private ProjectService projectService;

    private CollaboratorService collaboratorService;

    @BeforeEach
    void setUp() {
        projectRepository = mock(ProjectRepository.class);
        projectsToAccountsRepository = mock(ProjectsToAccountsRepository.class);
        collaboratorService = mock(CollaboratorService.class);
        securityUtils = mock(SecurityUtils.class);
        projectService = new ProjectService(projectRepository, securityUtils, projectsToAccountsRepository, collaboratorService);
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
        Project project1 = new Project("Title1", "Description1",  false);
        Project project2 = new Project("Title2", "Description2", false);
        Project project3 = new Project("Title3", "Description3", false);
        List<Project> projects = List.of(project1, project2, project3);

        when(projectRepository.findAll()).thenReturn(projects);

        List<Project> response = projectService.getProjects();
        assertEquals(projects, response);
    }

    @Test
    void deleteProjectSuccessful() {
        UUID projectId = UUID.randomUUID();
        Project project1 = new Project("Title1", "Description1", false);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project1));
        projectService.deleteProject(projectId);
        verify(projectRepository,times(1)).delete(project1);
    }
    @Test
    void updateProjectSuccess() {
        UUID projectId = UUID.randomUUID();
        Project project1 = new Project("Title1", "Description1",  false);
        Project project2 = new Project("Title2", "Description2", false);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project1));
        when(projectRepository.save(project1)).thenReturn(project2);
        Project response = projectService.updateProject(projectId, project2);
        assertEquals(project2, response);
    }
    @Test
    void createProjectSuccess() {
        String title = "title1";
        String desc = "desc1";
        Project project = new Project(title, desc, false);

        when(projectRepository.findFirstByTitleAndDescription(title, desc))
            .thenReturn(Optional.empty());
        when(projectRepository.save(any())).thenReturn(new Project(title, desc, false));
        when(projectsToAccountsRepository.save(any())).thenReturn(null);
        when(securityUtils.getCurrentUser()).thenReturn(new Account());

        Project response = projectService.createProject(project);
        assertEquals(project.getTitle(), response.getTitle());
        assertEquals(project.getDescription(), response.getDescription());
    }

    @Test
    void createProjectExistsAlready() {
        String title = "title1";
        String desc = "desc1";
        String bibtex = "bibtex1";
        Project project = new Project(title, desc, false);
        when(projectRepository.findFirstByTitleAndDescription(title, desc))
                .thenReturn(Optional.of(new Project(title, desc, false)));
        assertThrows(ResponseStatusException.class, () -> projectService.createProject(project));
    }

    @Test
    void getProjectByIdSuccess() {
        UUID projectId = UUID.randomUUID();
        Project project1 = new Project("Title1", "Description1", false);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project1));
        Project response = projectService.getProjectById(projectId);
        assertEquals(project1, response);
    }

    @Test
    void getProjectByIdNotFound() {
        UUID projectId = UUID.randomUUID();
        assertThrows(ResponseStatusException.class, () -> projectService.getProjectById(projectId));
    }

    @Test
    void testUserBelongsToProjectProjectNotFound() {
        UUID projectId = UUID.randomUUID();
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> projectService.userBelongsToProject("username",projectId));
    }

    @Test
    void testUserBelongsToProjectUserNotInProject() {
        UUID projectId = UUID.randomUUID();
        Project project = new Project("Title1", "Description1", false);
        project.setProjectsToAccounts(new ArrayList<>());
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        assertThrows(ResponseStatusException.class, () -> projectService.userBelongsToProject("username",projectId));
    }

    @Test
    void testUserBelongsToProjectUserInProject() {
        UUID projectId = UUID.randomUUID();
        Project project = new Project("Title1", "Description1", false);
        Account account = new Account();
        account.setUsername("username");
        project.setProjectsToAccounts(List.of(new ProjectsToAccounts(RoleInProject.PM, account, project)));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        RoleInProject response = projectService.userBelongsToProject("username",projectId);
        assertEquals(RoleInProject.PM, response);
    }

}