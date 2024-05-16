package com.team2a.ProjectPortfolio.Controllers;

import com.team2a.ProjectPortfolio.Commons.Link;
import com.team2a.ProjectPortfolio.Services.LinkService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinkControllerTest {
    @Mock
    private LinkService ls;

    @InjectMocks
    private LinkController lc;

    @Test
    void addLinkSuccess() {
        Link link = new Link("Test Link", "Test Description");
        link.setLinkId(UUID.randomUUID());
        when(ls.addLinkToProject(any(Link.class))).thenReturn(link);
        ResponseEntity<Link> responseEntity = lc.addLinkToProject(link);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(link, responseEntity.getBody());
        verify(ls, times(1)).addLinkToProject(any(Link.class));
    }

    @Test
    void addLinkConflict() {
        Link link = new Link("Test Link", "Test Description");
        link.setLinkId(UUID.randomUUID());
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT)).when(ls).addLinkToProject(any(Link.class));
        ResponseEntity<Link> responseEntity = lc.addLinkToProject(link);
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(ls, times(1)).addLinkToProject(any(Link.class));
    }

    @Test
    void editLinkSuccess() {
        Link link = new Link("Test Link", "Test Description");
        link.setLinkId(UUID.randomUUID());
        when(ls.editLinkOfProject(any(Link.class))).thenReturn(link);
        ResponseEntity<Link> responseEntity = lc.editLinkOfProject(link);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(link, responseEntity.getBody());
        verify(ls, times(1)).editLinkOfProject(any(Link.class));
    }
    @Test
    void editLinkBadRequest() {
        Link link = null;
        when(ls.editLinkOfProject(null)).thenThrow(IllegalArgumentException.class);
        ResponseEntity<Link> responseEntity = lc.editLinkOfProject(link);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(ls, times(1)).editLinkOfProject(null);
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
    void getLinksByProjectIdSuccess() {
        UUID projectId = UUID.randomUUID();
        Link link1 = new Link("link1", "desc1");
        Link link2 = new Link("link2", "desc2");
        when(ls.getLinksByProjectId(projectId)).thenReturn(List.of(link2));
        ResponseEntity<List<Link>> response = lc.getLinksByProjectId(projectId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(link2), response.getBody());
    }

    @Test
    void getLinksByProjectIdNullId() {
        when(ls.getLinksByProjectId(null)).thenThrow(IllegalArgumentException.class);
        ResponseEntity<List<Link>> response = lc.getLinksByProjectId(null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getLinksByProjectIdNotFound() {
        UUID projectId = UUID.randomUUID();
        when(ls.getLinksByProjectId(projectId)).thenThrow(EntityNotFoundException.class);
        ResponseEntity<List<Link>> response = lc.getLinksByProjectId(projectId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}
