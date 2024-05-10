package com.team2a.ProjectPortfolio.Controllers;

import com.team2a.ProjectPortfolio.Commons.Collaborator;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.ProjectsToCollaborators;
import com.team2a.ProjectPortfolio.Repositories.CollaboratorRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectsToCollaboratorsRepository;
import com.team2a.ProjectPortfolio.Services.CollaboratorService;
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
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
class CollaboratorControllerTest {
    @Autowired
    private transient ProjectsToCollaboratorsRepository projectsToCollaboratorsRepository;
    @Autowired
    private transient ProjectRepository projectRepository;
    @Autowired
    private transient CollaboratorRepository collaboratorRepository;
    private CollaboratorService collaboratorService;
    private CollaboratorController collaboratorController;

    @BeforeEach
    void setUp() {
        collaboratorService = new CollaboratorService(projectsToCollaboratorsRepository,collaboratorRepository,projectRepository);
        collaboratorController = new CollaboratorController(collaboratorService);
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
        ResponseEntity<List<Collaborator>> actualResponse = collaboratorController.getCollaboratorsByProjectId(project.getProjectId());
        assertEquals(HttpStatus.OK,actualResponse.getStatusCode());
        assertEquals(expectedResponse,actualResponse.getBody());
        assertEquals(1, actualResponse.getBody().size());
    }

    @Test
    void testGetCollaboratorsByProjectIdIllegal(){
        Project project = new Project("Test", "Test", "Test", false);
        project = projectRepository.save(project);
        ResponseEntity<List<Collaborator>> actualResponse = collaboratorController.getCollaboratorsByProjectId(null);
        assertEquals(HttpStatus.BAD_REQUEST,actualResponse.getStatusCode());
        assertEquals(null,actualResponse.getBody());
    }
    @Test
    void testGetCollaboratorsByProjectNoFound(){
        Project project = new Project("Test", "Test", "Test", false);
        project = projectRepository.save(project);
        ArrayList<Collaborator> expectedResponse = new ArrayList<>();
        ResponseEntity<List<Collaborator>> actualResponse = collaboratorController.getCollaboratorsByProjectId(UUID.randomUUID());
        assertEquals(HttpStatus.NOT_FOUND,actualResponse.getStatusCode());
        assertEquals(null,actualResponse.getBody());
    }

    @Test
    void testAddCollaboratorSuccess(){
        Project project = new Project("Test", "Test", "Test", false);
        project = projectRepository.save(project);
        Collaborator collaborator = new Collaborator("Filip");
        collaborator = collaboratorRepository.save(collaborator);
        ProjectsToCollaborators projectsToCollaborators = new ProjectsToCollaborators(project,collaborator);
        projectsToCollaborators = projectsToCollaboratorsRepository.save(projectsToCollaborators);
        ResponseEntity<Collaborator> actualResponse = collaboratorController.addCollaboratorToProject(project.getProjectId(),"Filip");
        assertEquals(HttpStatus.OK,actualResponse.getStatusCode());
        assertEquals(collaborator,actualResponse.getBody());
    }

    @Test
    void testAddCollaboratorNotFound(){
        Project project = new Project("Test", "Test", "Test", false);
        project = projectRepository.save(project);
        ResponseEntity<Collaborator> actualResponse = collaboratorController.addCollaboratorToProject(UUID.randomUUID(),"Filip");
        assertEquals(HttpStatus.NOT_FOUND,actualResponse.getStatusCode());
        assertEquals(null,actualResponse.getBody());
    }
    @Test
    void testAddCollaboratorIllegal(){
        Project project = new Project("Test", "Test", "Test", false);
        project = projectRepository.save(project);
        ResponseEntity<Collaborator> actualResponse = collaboratorController.addCollaboratorToProject(null,"Filip");
        assertEquals(HttpStatus.BAD_REQUEST,actualResponse.getStatusCode());
        assertEquals(null,actualResponse.getBody());
    }
    @Test
    void testAddCollaboratorNoResultCreate(){
        Project project = new Project("Test", "Test", "Test", false);
        project = projectRepository.save(project);
        Collaborator collaborator = new Collaborator("Filip");
        collaborator = collaboratorRepository.save(collaborator);
        ProjectsToCollaborators projectsToCollaborators = new ProjectsToCollaborators(project,collaborator);
        projectsToCollaborators = projectsToCollaboratorsRepository.save(projectsToCollaborators);
        assertEquals(1,collaboratorRepository.findAll().size());
        ResponseEntity<Collaborator> actualResponse = collaboratorController.addCollaboratorToProject(project.getProjectId(),"Andrei");
        assertEquals(2,collaboratorRepository.findAll().size());
        assertEquals(HttpStatus.OK,actualResponse.getStatusCode());
        assertEquals("Andrei",actualResponse.getBody().getName());
    }

    @Test
    void testEditCollaboratorSuccess(){
        Project project = new Project("Test", "Test", "Test", false);
        project = projectRepository.save(project);
        Collaborator collaborator = new Collaborator("Andrei");
        collaborator = collaboratorRepository.save(collaborator);
        ProjectsToCollaborators projectsToCollaborators = new ProjectsToCollaborators(project,collaborator);
        projectsToCollaborators = projectsToCollaboratorsRepository.save(projectsToCollaborators);
        assertEquals(1,collaboratorRepository.findAll().size());
        ResponseEntity<Collaborator> actualResponse = collaboratorController.editCollaboratorOfProject(collaborator.getCollaboratorId(),"Filip");
        assertEquals(1,collaboratorRepository.findAll().size());
        assertEquals(HttpStatus.OK,actualResponse.getStatusCode());
        assertEquals("Filip", Objects.requireNonNull(actualResponse.getBody()).getName());
        assertEquals(collaborator,collaboratorRepository.findById(collaborator.getCollaboratorId()).get());
        assertEquals("Filip",collaboratorRepository.findById(collaborator.getCollaboratorId()).get().getName());
    }
    @Test
    void testEditCollaboratorNotFound(){
        Project project = new Project("Test", "Test", "Test", false);
        project = projectRepository.save(project);
        Collaborator collaborator = new Collaborator("Andrei");
        collaborator = collaboratorRepository.save(collaborator);
        ProjectsToCollaborators projectsToCollaborators = new ProjectsToCollaborators(project,collaborator);
        projectsToCollaborators = projectsToCollaboratorsRepository.save(projectsToCollaborators);
        ResponseEntity<Collaborator> actualResponse = collaboratorController.editCollaboratorOfProject(UUID.randomUUID(),"Filip");
        assertEquals(HttpStatus.NOT_FOUND,actualResponse.getStatusCode());
        assertEquals(null,actualResponse.getBody());
    }
    @Test
    void testEditCollaboratorIllegal(){
        Project project = new Project("Test", "Test", "Test", false);
        project = projectRepository.save(project);
        ResponseEntity<Collaborator> actualResponse = collaboratorController.editCollaboratorOfProject(null,"Filip");
        assertEquals(HttpStatus.BAD_REQUEST,actualResponse.getStatusCode());
        assertEquals(null,actualResponse.getBody());
    }


}