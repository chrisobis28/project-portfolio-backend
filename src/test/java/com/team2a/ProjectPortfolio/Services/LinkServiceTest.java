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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.parameters.P;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
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
        Project project = new Project("test","test",false);
        UUID projectId  = UUID.randomUUID();
        project.setProjectId(projectId);
        link.setLinkId(UUID.randomUUID());
        link.setProject(new Project());
        link.getProject().setProjectId(UUID.randomUUID());
        when(pr.existsById(any(UUID.class))).thenReturn(true);
        when(lr.existsByProjectProjectIdAndUrl(any(UUID.class), any(String.class))).thenReturn(false);
        when(lr.saveAndFlush(any(Link.class))).thenReturn(link);
        when(pr.findById(any(UUID.class))).thenReturn(Optional.of(project));
        Link addedLink = ls.addLinkToProject(link,projectId);
        assertEquals(link, addedLink);
        verify(lr, times(1)).saveAndFlush(any(Link.class));
    }

    @Test
    void addLinkProjectNotFound(){
        Link link = new Link("Test","Test");
        UUID projectId  = UUID.randomUUID();
        link.setProject(new Project());
        link.getProject().setProjectId(UUID.randomUUID());
        when(pr.existsById(any(UUID.class))).thenReturn(false);
        assertThrows(ResponseStatusException.class, () -> ls.addLinkToProject(link,projectId));
        verify(lr, never()).saveAndFlush(any(Link.class));
    }

    @Test
    void addLinkConflict(){
        Link link = new Link("Test","Test");
        UUID projectId  = UUID.randomUUID();
        link.setProject(new Project());
        link.getProject().setProjectId(UUID.randomUUID());
        when(pr.existsById(any(UUID.class))).thenReturn(true);
        when(lr.existsByProjectProjectIdAndUrl(any(UUID.class), any(String.class))).thenReturn(true);
        assertThrows(ResponseStatusException.class, () -> ls.addLinkToProject(link,projectId));
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
    void deleteLinkByIdSuccess(){

        Link link = new Link("Test","Test");
        UUID id1 = UUID.randomUUID();
        Project p = new Project();
        p.setProjectId(id1);
        link.setProject(p);
        link.setLinkId(UUID.randomUUID());
        when(lr.findById(any(UUID.class))).thenReturn(Optional.of(link));
        String response = ls.deleteLinkById(link.getLinkId());
        assertEquals(id1.toString(), response);
        verify(lr, times(1)).findById(any(UUID.class));
    }
    @Test
    void editLinkNotFound() {
        Link link = new Link("Test","Test");
        link.setLinkId(UUID.randomUUID());
        when(lr.findById(any(UUID.class))).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> ls.editLinkOfProject(link));
        verify(lr, times(1)).findById(any(UUID.class));
        verify(lr, never()).save(any(Link.class));
    }
    @Test
    void getLinksByProjectIdSuccess() {
        UUID projectId = UUID.randomUUID();
        Link link2 = new Link("link2", "desc2");
        when(lr.findAllByProjectProjectId(projectId)).thenReturn(List.of(link2));
        List<Link> response = ls.getLinksByProjectId(projectId);
        assertEquals(List.of(link2), response);
    }
    @Test
    void getLinksByProjectIdNotFound() {
        UUID projectId = UUID.randomUUID();
        when(lr.findAllByProjectProjectId(projectId)).thenReturn(List.of());
        assertEquals(ls.getLinksByProjectId(projectId),List.of());
    }
    @Test
    void deleteLinkByIdNotFound() {
        when(lr.findById(any(UUID.class))).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> ls.deleteLinkById(UUID.randomUUID()));
    }

}