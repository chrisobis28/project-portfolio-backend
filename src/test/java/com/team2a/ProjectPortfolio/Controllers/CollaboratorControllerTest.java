package com.team2a.ProjectPortfolio.Controllers;

import com.team2a.ProjectPortfolio.Commons.Collaborator;
import com.team2a.ProjectPortfolio.Services.CollaboratorService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollaboratorControllerTest {
    @Mock
    private CollaboratorService cs;

    @InjectMocks
    private CollaboratorController cc;

    @Test
    void getCollaboratorsByProjectIdSuccess () {
        UUID projectId = UUID.randomUUID();
        List<Collaborator> expectedCollaborators = Collections.singletonList(new Collaborator("Filip"));
        when(cs.getCollaboratorsByProjectId(projectId)).thenReturn(expectedCollaborators);
        ResponseEntity<List<Collaborator>> responseEntity = cc.getCollaboratorsByProjectId(projectId);
        assertEquals(expectedCollaborators, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void getCollaboratorsByProjectIdNotFound () {
        UUID invalidProjectId = UUID.randomUUID();
        when(cs.getCollaboratorsByProjectId(invalidProjectId)).thenThrow(EntityNotFoundException.class);
        ResponseEntity<List<Collaborator>> responseEntity = cc.getCollaboratorsByProjectId(invalidProjectId);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void addCollaboratorToProjectSuccess () {
        UUID projectId = UUID.randomUUID();
        UUID collaboratorId = UUID.randomUUID();
        String role = "Role";
        Collaborator expectedCollaborator = new Collaborator("Andrei");
        when(cs.addCollaboratorToProject(projectId, collaboratorId,role)).thenReturn(expectedCollaborator);
        ResponseEntity<Collaborator> responseEntity = cc.addCollaboratorToProject(projectId, collaboratorId,role);
        assertEquals(expectedCollaborator, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }


    @Test
    void addCollaboratorToProjectINotFoundProject () {
        UUID invalidProjectId = UUID.randomUUID();
        UUID invalidCollaboratorId = UUID.randomUUID();
        String collaboratorName = "Filip";
        String role = "Role";
        when(cs.addCollaboratorToProject(invalidProjectId, invalidCollaboratorId,role)).thenThrow(EntityNotFoundException.class);
        ResponseEntity<Collaborator> responseEntity = cc.addCollaboratorToProject(invalidProjectId, invalidCollaboratorId,role);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void editCollaboratorOfProjectSuccess() {
        UUID collaboratorId = UUID.randomUUID ();
        String collaboratorName = "Filip";
        Collaborator expectedCollaborator = new Collaborator("Andrei");
        when(cs.editCollaboratorOfProject(collaboratorId, collaboratorName)).thenReturn(expectedCollaborator);
        ResponseEntity<Collaborator> responseEntity = cc.editCollaboratorOfProject(collaboratorId, collaboratorName);
        assertEquals(expectedCollaborator, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }


    @Test
    void editCollaboratorOfProjectINotFound () {
        UUID collaboratorId = UUID.randomUUID();
        String collaboratorName = "Filip";
        when(cs.editCollaboratorOfProject(collaboratorId, collaboratorName)).thenThrow(EntityNotFoundException.class);
        ResponseEntity<Collaborator> responseEntity = cc.editCollaboratorOfProject(collaboratorId, collaboratorName);
        assertNull(responseEntity.getBody());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void deleteCollaboratorSuccess () {
        UUID collaboratorId = UUID.randomUUID();
        when(cs.deleteCollaborator(collaboratorId)).thenReturn("Collaborator deleted");
        ResponseEntity<String> responseEntity = cc.deleteCollaborator(collaboratorId);
        assertEquals("Collaborator deleted", responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }


    @Test
    void deleteCollaboratorNotFound () {
        UUID collaboratorId = UUID.randomUUID();
        when(cs.deleteCollaborator(collaboratorId)).thenThrow(EntityNotFoundException.class);
        ResponseEntity<String> responseEntity = cc.deleteCollaborator(collaboratorId);
        assertNull(responseEntity.getBody());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void deleteCollaboratorFromProjectSuccess () {
        UUID projectId = UUID.randomUUID();
        UUID collaboratorId = UUID.randomUUID();
        when(cs.deleteCollaboratorFromProject(projectId, collaboratorId)).thenReturn("Collaborator deleted");
        ResponseEntity<String> responseEntity = cc.deleteCollaboratorFromProject(projectId, collaboratorId);
        assertEquals("Collaborator deleted", responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void deleteCollaboratorFromProjectNotFound () {
        UUID projectId = UUID.randomUUID();
        UUID collaboratorId = UUID.randomUUID();
        when(cs.deleteCollaboratorFromProject(projectId, collaboratorId)).thenThrow(EntityNotFoundException.class);
        ResponseEntity<String> responseEntity = cc.deleteCollaboratorFromProject(projectId, collaboratorId);
        assertNull(responseEntity.getBody());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}
