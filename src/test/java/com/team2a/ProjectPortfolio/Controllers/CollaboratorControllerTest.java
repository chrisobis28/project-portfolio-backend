package com.team2a.ProjectPortfolio.Controllers;

import com.team2a.ProjectPortfolio.Commons.Collaborator;
import com.team2a.ProjectPortfolio.Commons.RequestCollaboratorsProjects;
import com.team2a.ProjectPortfolio.Services.CollaboratorService;
import com.team2a.ProjectPortfolio.WebSocket.CollaboratorProjectWebSocketHandler;
import com.team2a.ProjectPortfolio.WebSocket.CollaboratorWebSocketHandler;
import com.team2a.ProjectPortfolio.dto.CollaboratorTransfer;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Test
    void testGetCollaboratorsRequestOk () {
        RequestCollaboratorsProjects rq = new RequestCollaboratorsProjects();
        when(cs.getCollaboratorsForRequest(any())).thenReturn(List.of(rq));
        ResponseEntity<List<RequestCollaboratorsProjects>> res = cc.getCollaboratorsForRequest(UUID.randomUUID(),
                UUID.randomUUID());
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), List.of(rq));
    }

    @Test
    void testGetCollaboratorRequestNotFound () {
        when(cs.getCollaboratorsForRequest(any())).thenThrow(new EntityNotFoundException());
        assertEquals(cc.getCollaboratorsForRequest(UUID.randomUUID(), UUID.randomUUID()).getStatusCode(),
                HttpStatus.NOT_FOUND);
    }

    @Test
    void testAddCollaboratorToRequestOk () {
        Collaborator c = new Collaborator();
        when(cs.addCollaboratorToRequest(any(),any(), any())).thenReturn(c);
        ResponseEntity<Collaborator> res = cc.addCollaboratorToRequest(UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(), false);

        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), c);
    }

    @Test
    void testAddCollaboratorToRequestNotFound () {
        when(cs.addCollaboratorToRequest(any(), any(), any())).thenThrow(new EntityNotFoundException());
        assertEquals(cc.addCollaboratorToRequest(UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(),
                false).getStatusCode(), HttpStatus.NOT_FOUND);
    }




    @Test
    void getCollaboratorsByProjectIdSuccess() {
        UUID projectId = UUID.randomUUID ();
        UUID collaboratorId1 = UUID.randomUUID();
        UUID collaboratorId2 = UUID.randomUUID();
        UUID collaboratorId3 = UUID.randomUUID();
        UUID collaboratorId4 = UUID.randomUUID();
        UUID collaboratorId5 = UUID.randomUUID();
        CollaboratorTransfer collaborator1 = new CollaboratorTransfer(collaboratorId1, "Luca", "ROLE_USER");
        CollaboratorTransfer collaborator2 = new CollaboratorTransfer(collaboratorId2, "Filip", "ROLE_USER");
        CollaboratorTransfer collaborator3 = new CollaboratorTransfer(collaboratorId3, "Paul", "ROLE_USER");
        CollaboratorTransfer collaborator4 = new CollaboratorTransfer(collaboratorId4, "Chris", "ROLE_USER");
        CollaboratorTransfer collaborator5 = new CollaboratorTransfer(collaboratorId5, "Bogdan", "ROLE_ADMIN");
        List<CollaboratorTransfer> collaborators = List.of(collaborator1, collaborator2,
                collaborator3, collaborator4, collaborator5);

        when(cs.getCollaboratorsByProjectId(projectId)).thenReturn(collaborators);
        ResponseEntity<List<CollaboratorTransfer>> responseEntity = cc.getCollaboratorsByProjectId(projectId);
        assertEquals(collaborators, responseEntity.getBody());
        assertEquals(5, Objects.requireNonNull(responseEntity.getBody()).size());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}
