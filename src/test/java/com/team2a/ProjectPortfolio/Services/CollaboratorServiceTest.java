package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Collaborator;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.ProjectsToCollaborators;
import com.team2a.ProjectPortfolio.Controllers.CollaboratorController;
import com.team2a.ProjectPortfolio.Repositories.CollaboratorRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectsToCollaboratorsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
@DataJpaTest
class CollaboratorServiceTest {
    @Autowired
    private transient ProjectsToCollaboratorsRepository projectsToCollaboratorsRepository;
    @Autowired
    private transient ProjectRepository projectRepository;
    @Autowired
    private transient CollaboratorRepository collaboratorRepository;
    private CollaboratorService collaboratorService;

    @BeforeEach
    void setUp() {
        collaboratorService = new CollaboratorService(projectsToCollaboratorsRepository,collaboratorRepository,projectRepository);
    }

    @Test
    void testGetCollaboratorsByProjectIdSuccess(){
        Project project = new Project("Test", "Test", "Test", false);
        project = projectRepository.save(project);
        Collaborator collaborator = new Collaborator("Filip");
        collaborator = collaboratorRepository.save(collaborator);
        ProjectsToCollaborators projectsToCollaborators = new ProjectsToCollaborators(project,collaborator);
        projectsToCollaborators = projectsToCollaboratorsRepository.save(projectsToCollaborators);
        ArrayList<Collaborator> expectedResponse = new ArrayList<>();
        expectedResponse.add(collaborator);
        List<Collaborator> actualResponse = collaboratorService.getCollaboratorsByProjectId(project.getProjectId());
        assertEquals(expectedResponse,actualResponse);
    }


    @Test
    void testGetCollaboratorsByProjectIdIllegal(){
        Project project = new Project("Test", "Test", "Test", false);
        project = projectRepository.save(project);
        assertThrows(IllegalArgumentException.class, () -> collaboratorService.getCollaboratorsByProjectId(null));
    }
    @Test
    void testGetCollaboratorsByProjectIdNotFound(){
        Project project = new Project("Test", "Test", "Test", false);
        project = projectRepository.save(project);
        assertThrows(EntityNotFoundException.class, () -> collaboratorService.getCollaboratorsByProjectId(UUID.randomUUID()));
    }
    @Test
    void testGetCollaboratorsByProjectNoResult(){
        Project project = new Project("Test", "Test", "Test", false);
        project = projectRepository.save(project);
        assertEquals(new ArrayList<>(), collaboratorService.getCollaboratorsByProjectId(project.getProjectId()));
    }

    @Test
    void testAddCollaboratorSuccess(){
        Project project = new Project("Test", "Test", "Test", false);
        project = projectRepository.save(project);
        Collaborator collaborator = new Collaborator("Filip");
        collaborator = collaboratorRepository.save(collaborator);
        ProjectsToCollaborators projectsToCollaborators = new ProjectsToCollaborators(project,collaborator);
        projectsToCollaborators = projectsToCollaboratorsRepository.save(projectsToCollaborators);
        ArrayList<Collaborator> expectedResponse = new ArrayList<>();
        expectedResponse.add(collaborator);
        List<Collaborator> actualResponse = collaboratorService.getCollaboratorsByProjectId(project.getProjectId());
        assertEquals(expectedResponse,actualResponse);
    }

    @Test
    void testAddCollaboratorNotFound(){
        Project project = new Project("Test", "Test", "Test", false);
        project = projectRepository.save(project);
        assertThrows(IllegalArgumentException.class, () -> collaboratorService.addCollaboratorToProject(null,"Test"));
    }
    @Test
    void testAddCollaboratorIllegal(){
        Project project = new Project("Test", "Test", "Test", false);
        project = projectRepository.save(project);
        assertThrows(EntityNotFoundException.class, () -> collaboratorService.addCollaboratorToProject(UUID.randomUUID(),"Test"));
    }
    @Test
    void testAddCollaboratorNoResultCreate(){
        Project project = new Project("Test", "Test", "Test", false);
        project = projectRepository.save(project);
        Collaborator collaborator = new Collaborator("Filip");
        collaborator = collaboratorRepository.save(collaborator);
        ProjectsToCollaborators projectsToCollaborators = new ProjectsToCollaborators(project,collaborator);
        projectsToCollaborators = projectsToCollaboratorsRepository.save(projectsToCollaborators);
        ArrayList<Collaborator> expectedResponse = new ArrayList<>();
        expectedResponse.add(collaborator);
        assertEquals(1,collaboratorRepository.findAll().size());
        Collaborator actualResponse = collaboratorService.addCollaboratorToProject(project.getProjectId(),"Andrei");
        assertEquals(2,collaboratorRepository.findAll().size());
        assertEquals("Andrei",actualResponse.getName());
    }
    @Test
    void testEditCollaboratorSuccess(){
        Project project = new Project("Test", "Test", "Test", false);
        project = projectRepository.save(project);
        Collaborator collaborator = new Collaborator("Filip");
        collaborator = collaboratorRepository.save(collaborator);
        ProjectsToCollaborators projectsToCollaborators = new ProjectsToCollaborators(project,collaborator);
        projectsToCollaborators = projectsToCollaboratorsRepository.save(projectsToCollaborators);;
        Collaborator actualResponse = collaboratorService.editCollaboratorOfProject(collaborator.getCollaboratorId(),"Andrei");
        assertEquals("Andrei",collaboratorRepository.findById(collaborator.getCollaboratorId()).get().getName());
    }

    @Test
    void testEditCollaboratorNotFound(){
        Project project = new Project("Test", "Test", "Test", false);
        project = projectRepository.save(project);
        assertThrows(IllegalArgumentException.class, () -> collaboratorService.editCollaboratorOfProject(null,"Test"));
    }
    @Test
    void testEditCollaboratorIllegal(){
        Project project = new Project("Test", "Test", "Test", false);
        project = projectRepository.save(project);
        assertThrows(EntityNotFoundException.class, () -> collaboratorService.editCollaboratorOfProject(UUID.randomUUID(),"Test"));
    }
}