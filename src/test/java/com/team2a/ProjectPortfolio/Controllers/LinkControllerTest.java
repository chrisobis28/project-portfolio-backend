package com.team2a.ProjectPortfolio.Controllers;

import com.team2a.ProjectPortfolio.Commons.Link;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.RequestLinkProject;
import com.team2a.ProjectPortfolio.CustomExceptions.NotFoundException;
import com.team2a.ProjectPortfolio.Services.LinkService;
import com.team2a.ProjectPortfolio.WebSocket.LinkProjectWebSocketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinkControllerTest {
    @Mock
    private LinkService ls;

    @Mock
    private LinkProjectWebSocketHandler linkProjectWebSocketHandler;

    private LinkController lc;

    @BeforeEach
    void setup() {
        ls = Mockito.mock(LinkService.class);
        linkProjectWebSocketHandler = Mockito.mock(LinkProjectWebSocketHandler.class);
        lc = new LinkController(ls, linkProjectWebSocketHandler);
    }

    @Test
    void addLinkSuccess() {
        Link link = new Link("Test Link", "Test Description");
        UUID projectId = UUID.randomUUID();
        link.setLinkId(UUID.randomUUID());
        when(ls.addLinkToProject(any(Link.class),any(UUID.class))).thenReturn(link);
        ResponseEntity<Link> responseEntity = lc.addLinkToProject(link,projectId);
        verify(linkProjectWebSocketHandler).broadcast(any());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(link, responseEntity.getBody());
        verify(ls, times(1)).addLinkToProject(any(Link.class),any(UUID.class));
    }

    @Test
    void addLinkConflict() {
        Link link = new Link("Test Link", "Test Description");
        UUID projectId = UUID.randomUUID();
        link.setLinkId(UUID.randomUUID());
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT)).when(ls).addLinkToProject(any(Link.class),any(UUID.class));
        ResponseEntity<Link> responseEntity = lc.addLinkToProject(link,projectId);
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(ls, times(1)).addLinkToProject(any(Link.class),any(UUID.class));
    }

    @Test
    void editLinkSuccess() {
        Link link = new Link("Test Link", "Test Description");
        Project p = new Project();
        p.setProjectId(UUID.randomUUID());
        link.setProject(p);
        link.setLinkId(UUID.randomUUID());
        when(ls.editLinkOfProject(any(Link.class))).thenReturn(link);
        ResponseEntity<Link> responseEntity = lc.editLinkOfProject(link);
        verify(linkProjectWebSocketHandler).broadcast(any());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(link, responseEntity.getBody());
        verify(ls, times(1)).editLinkOfProject(any(Link.class));
    }
    @Test
    void editLinkNotFound() {
        Link link = new Link("Test Link", "Test Description");
        link.setLinkId(UUID.randomUUID());
        when(ls.editLinkOfProject(any(Link.class))).thenThrow(EntityNotFoundException.class);
        ResponseEntity<Link> responseEntity = lc.editLinkOfProject(link);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(ls, times(1)).editLinkOfProject(any(Link.class));
    }

    @Test
    void getLinksByProjectIdSuccess () {
        UUID projectId = UUID.randomUUID();
        Link link2 = new Link("link2", "desc2");
        when(ls.getLinksByProjectId(projectId)).thenReturn(List.of(link2));
        ResponseEntity<List<Link>> response = lc.getLinksByProjectId(projectId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(link2), response.getBody());
    }

    @Test
    void DeleteLinkByIdSuccess () {
        UUID linkId = UUID.randomUUID();
        when(ls.deleteLinkById(linkId)).thenReturn("Link deleted");
        ResponseEntity<String> response = lc.deleteLinkById(linkId);
        verify(linkProjectWebSocketHandler).broadcast(any());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Link deleted", response.getBody());
    }

    @Test
    void getLinksByProjectIdNotFound () {
        UUID projectId = UUID.randomUUID();
        when(ls.getLinksByProjectId(projectId)).thenThrow(EntityNotFoundException.class);
        ResponseEntity<List<Link>> response = lc.getLinksByProjectId(projectId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
    @Test
    void deleteLinkByIdNotFound () {
        UUID linkId = UUID.randomUUID();
        when(ls.deleteLinkById(linkId)).thenThrow(EntityNotFoundException.class);
        ResponseEntity<String> response = lc.deleteLinkById(linkId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getLinksRequestOk () {
        RequestLinkProject req = new RequestLinkProject();
        UUID newId = UUID.randomUUID();
        when(ls.getLinksForRequest(any())).thenReturn(List.of(req));
        ResponseEntity<List<RequestLinkProject>> res = lc.getLinksForRequest(UUID.randomUUID(),
                UUID.randomUUID());
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), List.of(req));
    }

    @Test
    void getLinksRequestNotFound () {
        when(ls.getLinksForRequest(any())).thenThrow(EntityNotFoundException.class);
        ResponseEntity<List<RequestLinkProject>> res = lc.getLinksForRequest(UUID.randomUUID(), UUID.randomUUID());
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void addAddedLinksOk () {
        Link l = new Link();
        when(ls.addAddedLinkToRequest(any(), any())).thenReturn(l);
        ResponseEntity<Link> body = lc.addAddedLinkToRequest(UUID.randomUUID(), l, UUID.randomUUID());
        assertEquals(body.getBody(), l);
        assertEquals(body.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testAddedLinksNotFound () {
        when(ls.addAddedLinkToRequest(any(), any())).thenThrow(new NotFoundException());
        ResponseEntity<Link> res = lc.addAddedLinkToRequest(UUID.randomUUID(), new Link(), UUID.randomUUID());
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testRemovedLinksOk () {
        Link l = new Link();
        when(ls.addRemovedLinkToRequest(any(), any())).thenReturn(l);
        ResponseEntity<Link> body = lc.addRemovedLinkToRequest(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        assertEquals(body.getBody(), l);
        assertEquals(body.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testRemovedLinksNotFound () {
        when(ls.addRemovedLinkToRequest(any(), any())).thenThrow(new NotFoundException());
        ResponseEntity<Link> res = lc.addRemovedLinkToRequest(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

}
