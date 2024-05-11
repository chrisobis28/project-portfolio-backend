package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Repositories.LinkRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.team2a.ProjectPortfolio.Commons.Link;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LinkServiceTest {

    @Mock
    private ProjectRepository pr;
    @Mock
    private LinkRepository lr;
    @InjectMocks
    private LinkService ls;

    @BeforeEach
    void setUp () {
        ls = new LinkService(lr,pr);
    }

    @Test
    void addLinkSuccess(){
        Link link = new Link("Test","Test");
        link.setLinkId(UUID.randomUUID());
        link.setProject(new Project());
        link.getProject().setProjectId(UUID.randomUUID());
        when(pr.existsById(any(UUID.class))).thenReturn(true);
        when(lr.existsByProjectProjectIdAndUrl(any(UUID.class), any(String.class))).thenReturn(false);
        when(lr.saveAndFlush(any(Link.class))).thenReturn(link);
        Link addedLink = ls.addLinkToProject(link);
        assertEquals(link, addedLink);
        verify(lr, times(1)).saveAndFlush(any(Link.class));
    }

    @Test
    void addLinkProjectNotFound(){
        Link link = new Link("Test","Test");
        link.setProject(new Project());
        link.getProject().setProjectId(UUID.randomUUID());
        when(pr.existsById(any(UUID.class))).thenReturn(false);
        assertThrows(ResponseStatusException.class, () -> ls.addLinkToProject(link));
        verify(lr, never()).saveAndFlush(any(Link.class));
    }

    @Test
    void addLinkConflict(){
        Link link = new Link("Test","Test");
        link.setProject(new Project());
        link.getProject().setProjectId(UUID.randomUUID());
        when(pr.existsById(any(UUID.class))).thenReturn(true);
        when(lr.existsByProjectProjectIdAndUrl(any(UUID.class), any(String.class))).thenReturn(true);
        assertThrows(ResponseStatusException.class, () -> ls.addLinkToProject(link));
        verify(lr, never()).saveAndFlush(any(Link.class));
    }

    @Test
    void editLinkSuccess(){
        Link link = new Link("Test","Test");
        link.setLinkId(UUID.randomUUID());
        when(lr.findById(any(UUID.class))).thenReturn(Optional.of(link));
        when(lr.save(any(Link.class))).thenReturn(link);
        Link editedLink = ls.editLinkOfProject(link);
        assertEquals(link, editedLink);
        verify(lr, times(1)).findById(any(UUID.class));
        verify(lr, times(1)).save(any(Link.class));
    }
    @Test
    void editLinkNotfound() {
        Link link = new Link("Test","Test");
        link.setLinkId(UUID.randomUUID());
        when(lr.findById(any(UUID.class))).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> ls.editLinkOfProject(link));
        verify(lr, times(1)).findById(any(UUID.class));
        verify(lr, never()).save(any(Link.class));
    }

    @Test
    void editLinkNull() {
        assertThrows(IllegalArgumentException.class, () -> ls.editLinkOfProject(null));
        verify(lr, never()).findById(any(UUID.class));
        verify(lr, never()).save(any(Link.class));
    }

}