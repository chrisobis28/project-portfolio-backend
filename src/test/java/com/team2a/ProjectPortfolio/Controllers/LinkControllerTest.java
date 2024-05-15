package com.team2a.ProjectPortfolio.Controllers;

import com.team2a.ProjectPortfolio.Commons.Link;
import com.team2a.ProjectPortfolio.Services.LinkService;
import com.team2a.ProjectPortfolio.Services.LinkServiceTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.team2a.ProjectPortfolio.Repositories.LinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.team2a.ProjectPortfolio.Commons.Link;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
}
