package com.team2a.ProjectPortfolio.unit.Controllers;

import com.team2a.ProjectPortfolio.Commons.Collaborator;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.ProjectsToCollaborators;
import com.team2a.ProjectPortfolio.Controllers.CollaboratorController;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
class CollaboratorControllerAutowired {
    @Autowired
    private ProjectsToCollaboratorsRepository projectsToCollaboratorsRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private CollaboratorRepository collaboratorRepository;
    private CollaboratorService cs;
    private CollaboratorController cc;
    private Project project;
    @BeforeEach
    void setUp () {
        project = new Project ("Test", "Test", "Test", false);
        project = projectRepository.save(project);
        cs = new CollaboratorService (projectsToCollaboratorsRepository, collaboratorRepository,
                projectRepository);
        cc = new CollaboratorController (cs);
    }

    @Test
    void testGetCollaboratorsByProjectIdSuccess () {
        Collaborator collaborator = new Collaborator("Filip");
        collaborator = collaboratorRepository.save(collaborator);
        ProjectsToCollaborators projectsToCollaborators = new ProjectsToCollaborators(project, collaborator);
        projectsToCollaborators = projectsToCollaboratorsRepository.save(projectsToCollaborators);
        ArrayList<Collaborator> expectedResponse = new ArrayList<>();
        expectedResponse.add(collaborator);
        ResponseEntity<List<Collaborator>> actualResponse = cc.
                getCollaboratorsByProjectId(project.getProjectId());
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(expectedResponse, actualResponse.getBody());
        assertEquals(1, actualResponse.getBody().size());
    }

    @Test
    void testGetCollaboratorsByProjectIdIllegal () {
        ResponseEntity<List<Collaborator>> actualResponse = cc.getCollaboratorsByProjectId(null);
        assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
        assertNull(actualResponse.getBody());
    }

    @Test
    void testGetCollaboratorsByProjectNoFound () {
        ArrayList<Collaborator> expectedResponse = new ArrayList<>();
        ResponseEntity<List<Collaborator>> actualResponse = cc.
                getCollaboratorsByProjectId(UUID.randomUUID());
        assertEquals(HttpStatus.NOT_FOUND, actualResponse.getStatusCode());
        assertNull(actualResponse.getBody());
    }

    @Test
    void testAddCollaboratorSuccess () {
        Collaborator collaborator = new Collaborator("Filip");
        collaborator = collaboratorRepository.save(collaborator);
        ProjectsToCollaborators projectsToCollaborators = new ProjectsToCollaborators(project, collaborator);
        projectsToCollaborators = projectsToCollaboratorsRepository.save(projectsToCollaborators);
        ResponseEntity<Collaborator> actualResponse = cc.
                addCollaboratorToProject(project.getProjectId(), "Filip");
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(collaborator, actualResponse.getBody());
    }

    @Test
    void testAddCollaboratorNotFound () {
        ResponseEntity<Collaborator> actualResponse = cc.
                addCollaboratorToProject(UUID.randomUUID(), "Filip");
        assertEquals(HttpStatus.NOT_FOUND, actualResponse.getStatusCode());
        assertNull(actualResponse.getBody());
    }

    @Test
    void testAddCollaboratorIllegal () {
        ResponseEntity<Collaborator> actualResponse = cc.
                addCollaboratorToProject(null, "Filip");
        assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
        assertNull(actualResponse.getBody());
    }

    @Test
    void testAddCollaboratorNoResultCreate () {
        Collaborator collaborator = new Collaborator("Filip");
        collaborator = collaboratorRepository.save(collaborator);
        ProjectsToCollaborators projectsToCollaborators = new ProjectsToCollaborators(project, collaborator);
        projectsToCollaborators = projectsToCollaboratorsRepository.save(projectsToCollaborators);
        assertEquals(1, collaboratorRepository.findAll().size());
        ResponseEntity<Collaborator> actualResponse = cc.
                addCollaboratorToProject(project.getProjectId(), "Andrei");
        assertEquals(2, collaboratorRepository.findAll().size());
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals("Andrei", actualResponse.getBody().getName());
    }

    @Test
    void testEditCollaboratorSuccess () {
        Collaborator collaborator = new Collaborator("Andrei");
        collaborator = collaboratorRepository.save(collaborator);
        ProjectsToCollaborators projectsToCollaborators = new ProjectsToCollaborators(project, collaborator);
        projectsToCollaborators = projectsToCollaboratorsRepository.save(projectsToCollaborators);
        assertEquals(1, collaboratorRepository.findAll().size());
        ResponseEntity<Collaborator> actualResponse = cc.
                editCollaboratorOfProject(collaborator.getCollaboratorId(), "Filip");
        assertEquals(1, collaboratorRepository.findAll().size());
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals("Filip", Objects.requireNonNull(actualResponse.getBody()).getName());
        assertEquals(collaborator, collaboratorRepository.findById(collaborator.getCollaboratorId()).get());
        assertEquals("Filip", collaboratorRepository.findById(collaborator.getCollaboratorId()).get().getName());
    }

    @Test
    void testEditCollaboratorNotFound () {
        Collaborator collaborator = new Collaborator("Andrei");
        collaborator = collaboratorRepository.save(collaborator);
        ProjectsToCollaborators projectsToCollaborators = new ProjectsToCollaborators(project, collaborator);
        projectsToCollaborators = projectsToCollaboratorsRepository.save(projectsToCollaborators);
        ResponseEntity<Collaborator> actualResponse = cc.
                editCollaboratorOfProject(UUID.randomUUID(), "Filip");
        assertEquals(HttpStatus.NOT_FOUND, actualResponse.getStatusCode());
        assertNull(actualResponse.getBody());
    }

    @Test
    void testEditCollaboratorIllegal () {
        ResponseEntity<Collaborator> actualResponse = cc.
                editCollaboratorOfProject(null, "Filip");
        assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
        assertNull(actualResponse.getBody());
    }

    @Test
    void testDeleteCollaboratorSuccess () {
        Collaborator collaborator = new Collaborator("Filip");
        collaborator = collaboratorRepository.save(collaborator);
        ResponseEntity<String> response = cc.deleteCollaborator(collaborator.getCollaboratorId());
        assertEquals("Deleted collaborator", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(), collaboratorRepository.findAll());
        Collaborator finalCollaborator = collaborator;
        assertEquals(HttpStatus.NOT_FOUND, cc.
                getCollaboratorsByProjectId(finalCollaborator.getCollaboratorId()).getStatusCode());
    }

    @Test
    void testDeleteCollaboratorNotFound () {
        Collaborator collaborator = new Collaborator("Filip");
        Collaborator collaboratorNew = collaboratorRepository.save(collaborator);
        assertEquals(HttpStatus.NOT_FOUND, cc.
                deleteCollaborator(UUID.randomUUID()).getStatusCode());
    }

    @Test
    void testDeleteCollaboratorIllegal () {
        assertEquals(HttpStatus.BAD_REQUEST, cc.
                deleteCollaborator(null).getStatusCode());
    }

    @Test
    void testDeleteCollaboratorFromProjectSuccess () {
        Collaborator collaborator = new Collaborator("Filip");
        collaborator = collaboratorRepository.save(collaborator);
        ProjectsToCollaborators projectsToCollaborators = new ProjectsToCollaborators(project, collaborator);
        projectsToCollaborators = projectsToCollaboratorsRepository.save(projectsToCollaborators);
        ResponseEntity<String> response = cc.
                deleteCollaboratorFromProject(project.getProjectId(), collaborator.getCollaboratorId());
        assertEquals("Deleted collaborator", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, collaboratorRepository.findAll().size());
        assertEquals(1, projectRepository.findAll().size());
        assertEquals(List.of(), projectsToCollaboratorsRepository.findAll());

    }

    @Test
    void testDeleteCollaboratorFromProjectNotFoundProject () {
        Collaborator collaborator = new Collaborator("Filip");
        collaborator = collaboratorRepository.save(collaborator);
        ProjectsToCollaborators projectsToCollaborators = new ProjectsToCollaborators(project, collaborator);
        projectsToCollaborators = projectsToCollaboratorsRepository.save(projectsToCollaborators);
        ResponseEntity<String> response = cc.
                deleteCollaboratorFromProject(UUID.randomUUID(), collaborator.getCollaboratorId());
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(1, collaboratorRepository.findAll().size());
        assertEquals(1, projectRepository.findAll().size());
        assertEquals(1, projectsToCollaboratorsRepository.findAll().size());
    }

    @Test
    void testDeleteCollaboratorFromProjectNotFoundCollaborator () {
        Collaborator collaborator = new Collaborator("Filip");
        collaborator = collaboratorRepository.save(collaborator);
        ProjectsToCollaborators projectsToCollaborators = new ProjectsToCollaborators(project, collaborator);
        projectsToCollaborators = projectsToCollaboratorsRepository.save(projectsToCollaborators);
        ResponseEntity<String> response = cc.
                deleteCollaboratorFromProject(project.getProjectId(), UUID.randomUUID());
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(1, collaboratorRepository.findAll().size());
        assertEquals(1, projectRepository.findAll().size());
        assertEquals(1, projectsToCollaboratorsRepository.findAll().size());
    }

    @Test
    void testDeleteCollaboratorFromProjectIProjectIllegal () {
        Collaborator collaborator = new Collaborator("Filip");
        collaborator = collaboratorRepository.save(collaborator);
        ProjectsToCollaborators projectsToCollaborators = new ProjectsToCollaborators(project, collaborator);
        projectsToCollaborators = projectsToCollaboratorsRepository.save(projectsToCollaborators);
        ResponseEntity<String> response = cc.deleteCollaboratorFromProject(null, collaborator.getCollaboratorId());
        assertNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, collaboratorRepository.findAll().size());
        assertEquals(1, projectRepository.findAll().size());
        assertEquals(1, projectsToCollaboratorsRepository.findAll().size());
    }

    @Test
    void testDeleteCollaboratorFromProjectICollaboratorIllegal () {
        Collaborator collaborator = new Collaborator("Filip");
        collaborator = collaboratorRepository.save(collaborator);
        ProjectsToCollaborators projectsToCollaborators = new ProjectsToCollaborators(project, collaborator);
        projectsToCollaborators = projectsToCollaboratorsRepository.save(projectsToCollaborators);
        ResponseEntity<String> response = cc.deleteCollaboratorFromProject(collaborator.getCollaboratorId(), null);
        assertNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, collaboratorRepository.findAll().size());
        assertEquals(1, projectRepository.findAll().size());
        assertEquals(1, projectsToCollaboratorsRepository.findAll().size());
    }
}