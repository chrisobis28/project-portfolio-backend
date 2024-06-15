package com.team2a.ProjectPortfolio.Controllers;

import com.team2a.ProjectPortfolio.Commons.Collaborator;
import com.team2a.ProjectPortfolio.Services.CollaboratorService;
import com.team2a.ProjectPortfolio.WebSocket.CollaboratorProjectWebSocketHandler;
import com.team2a.ProjectPortfolio.WebSocket.CollaboratorWebSocketHandler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollaboratorControllerTest {
    @Mock
    private CollaboratorService cs;

    private CollaboratorController cc;

    @Mock
    private CollaboratorWebSocketHandler collaboratorWebSocketHandler;

    @Mock
    private CollaboratorProjectWebSocketHandler collaboratorProjectWebSocketHandler;

    @BeforeEach
    void setup() {
        cs = Mockito.mock(CollaboratorService.class);
        collaboratorWebSocketHandler = Mockito.mock(CollaboratorWebSocketHandler.class);
        collaboratorProjectWebSocketHandler = Mockito.mock(CollaboratorProjectWebSocketHandler.class);
        cc = new CollaboratorController(cs, collaboratorWebSocketHandler, collaboratorProjectWebSocketHandler);

    }

    @Test
    void editCollaboratorOfProjectSuccess() {
        UUID collaboratorId = UUID.randomUUID ();
        String collaboratorName = "Filip";
        Collaborator expectedCollaborator = new Collaborator("Andrei");
        when(cs.editCollaboratorOfProject(collaboratorId, collaboratorName)).thenReturn(expectedCollaborator);
        ResponseEntity<Collaborator> responseEntity = cc.editCollaboratorOfProject(collaboratorId, collaboratorName);
        verify(collaboratorWebSocketHandler).broadcast(any());
        verify(collaboratorProjectWebSocketHandler).broadcast(any());
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
        verify(collaboratorWebSocketHandler).broadcast(any());
        verify(collaboratorProjectWebSocketHandler).broadcast(any());
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
        verify(collaboratorProjectWebSocketHandler).broadcast(any());
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

    @Test
    void testAddCollaborator () {

        Collaborator c1 = new Collaborator("coll1");
        when(cs.addCollaborator("coll1")).thenReturn(c1);
        ResponseEntity<Collaborator> res = cc.addCollaborator("coll1");
        verify(collaboratorWebSocketHandler).broadcast(any());
        assertEquals(res.getBody(), c1);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testGetCollaborators () {
        Collaborator c1 = new Collaborator("coll1");
        when(cs.getAllCollaborators()).thenReturn(List.of(c1));
        ResponseEntity<List<Collaborator>> res = cc.getAllCollaborators();
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), List.of(c1));
    }
}
