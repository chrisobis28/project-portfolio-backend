package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Repositories.LinkRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LinkServiceTest {
    @Mock
    private LinkRepository lr;
    @InjectMocks
    private LinkService ls;

    @BeforeEach
    void setUp () {
        ls = new LinkService(lr);
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