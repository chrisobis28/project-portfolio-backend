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
        collaboratorService = new CollaboratorService(projectsToCollaboratorsRepository);
        collaboratorController = new CollaboratorController(collaboratorService);
    }
    @Test
    void testGetCollaboratorsByProjectIdSuccess(){
        
        Project project = new Project("Test", "Test", "Test", false, null, null);
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
    void testGetCollaboratorsByProjectIdError(){

        Project project = new Project("Test", "Test", "Test", false,  null, null);
        project = projectRepository.save(project);

        Collaborator collaborator = new Collaborator("Filip");
        collaborator = collaboratorRepository.save(collaborator);

        ProjectsToCollaborators projectsToCollaborators = new ProjectsToCollaborators(project,collaborator);
        projectsToCollaborators = projectsToCollaboratorsRepository.save(projectsToCollaborators);


        ArrayList<Collaborator> expectedResponse = new ArrayList<>();
        ResponseEntity<List<Collaborator>> actualResponse = collaboratorController.getCollaboratorsByProjectId(null);

        assertEquals(HttpStatus.BAD_REQUEST,actualResponse.getStatusCode());
        assertEquals(null,actualResponse.getBody());
    }
    @Test
    void testGetCollaboratorsByProjectNoResult(){

        Project project = new Project("Test", "Test", "Test", false,  null, null);
        project = projectRepository.save(project);

        Collaborator collaborator = new Collaborator("Filip");
        collaborator = collaboratorRepository.save(collaborator);

        ProjectsToCollaborators projectsToCollaborators = new ProjectsToCollaborators(project,collaborator);
        projectsToCollaborators = projectsToCollaboratorsRepository.save(projectsToCollaborators);


        ArrayList<Collaborator> expectedResponse = new ArrayList<>();
        ResponseEntity<List<Collaborator>> actualResponse = collaboratorController.getCollaboratorsByProjectId(UUID.randomUUID());

        assertEquals(HttpStatus.NOT_FOUND,actualResponse.getStatusCode());
        assertEquals(null,actualResponse.getBody());
    }


}