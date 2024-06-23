package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.*;
import com.team2a.ProjectPortfolio.CustomExceptions.NotFoundException;
import com.team2a.ProjectPortfolio.Repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestServiceTest {

    private RequestService sut;

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TagToProjectRepository tagToProjectRepository;

    @Mock
    private ProjectsToCollaboratorsRepository projectsToCollaboratorsRepository;

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private LinkRepository linkRepository;

    @BeforeEach
    void setup() {
        sut = new RequestService();
        accountRepository = Mockito.mock(AccountRepository.class);
        requestRepository = Mockito.mock(RequestRepository.class);
        projectRepository = Mockito.mock(ProjectRepository.class);
        tagToProjectRepository = mock(TagToProjectRepository.class);
        projectsToCollaboratorsRepository = mock(ProjectsToCollaboratorsRepository.class);
        mediaRepository = mock(MediaRepository.class);
        linkRepository = mock(LinkRepository.class);
        sut.setAccountRepository(accountRepository);
        sut.setRequestRepository(requestRepository);
        sut.setProjectRepository(projectRepository);
        sut.setTagToProjectRepository(tagToProjectRepository);
        sut.setProjectsToCollaboratorsRepository(projectsToCollaboratorsRepository);
        sut.setMediaRepository(mediaRepository);
        sut.setLinkRepository(linkRepository);
    }

    @Test
    void testGetRequestsForUserEmptyUsername() {
        assertThrows(ResponseStatusException.class, () -> sut.getRequestsForUser(null));
    }

    @Test
    void testGetRequestForUserUserNotFound() {
        when(accountRepository.findById("Name")).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> sut.getRequestsForUser("Name"));
    }

    @Test
    void testGetRequestForUserOk() {
        Account a = new Account("uname", "Name", "pw", Role.ROLE_USER);
        Request r = new Request("title", "desc", true, a, new Project());
        a.setRequests(List.of(r));
        when(accountRepository.findById("uname")).thenReturn(Optional.of(a));
        assertEquals(sut.getRequestsForUser("uname"), List.of(r));
    }

    @Test
    void testGetRequests() {
        Request r = new Request("title", "description", false, new Account(), new Project());
        when(requestRepository.findAll()).thenReturn(List.of(r));
        assertEquals(sut.getRequests(), List.of(r));
    }

    @Test
    void testAddRequestOk() {
        Project p = new Project("title", "desc", false);
        Account a = new Account("uname", "Name", "pw", Role.ROLE_USER);
        Request r = new Request("title", "description", false, a , p);
        when(projectRepository.findById(p.getProjectId())).thenReturn(Optional.of(p));
        when(accountRepository.findById(a.getUsername())).thenReturn(Optional.of(a));
        when(requestRepository.save(r)).thenReturn(r);
        assertEquals(sut.addRequest(r), r);
    }

    @Test
    void testAddRequestProjectNotFound() {
        Request r = new Request();
        r.setProject(new Project());
        when(projectRepository.findById(r.getProject().getProjectId())).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> sut.addRequest(r));
    }

    @Test
    void testAddRequestAccountNotFound() {
        Project p = new Project("title", "desc", false);
        Account a = new Account("uname", "Name", "pw", Role.ROLE_USER);
        Request r = new Request("title", "description", false, a , p);
        when(projectRepository.findById(p.getProjectId())).thenReturn(Optional.of(p));
        when(accountRepository.findById(a.getUsername())).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> sut.addRequest(r));
    }

    @Test
    void testAddRequestConflict() {
        Project p = new Project("title", "desc", false);
        Account a = new Account("uname", "Name", "pw", Role.ROLE_USER) {
            @Override
            public boolean hasRequestForProject(UUID projectId) {
                return true;
            }
        };
        Request r = new Request("title", "description", false, a , p);
        when(projectRepository.findById(p.getProjectId())).thenReturn(Optional.of(p));
        when(accountRepository.findById(a.getUsername())).thenReturn(Optional.of(a));
        assertThrows(ResponseStatusException.class, () -> sut.addRequest(r));
    }

    @Test
    void testGetRequestsForProjectNull() {
        assertThrows(ResponseStatusException.class, () -> sut.getRequestsForProject(null));
    }

    @Test
    void testGetRequestsForProjectNotFound() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        while(id2.equals(id1)){
            id2 = UUID.randomUUID();
        }
        Project p = new Project("title", "desc", false);
        p.setProjectId(id1);
        when(projectRepository.findAll()).thenReturn(List.of(p));
        UUID finalId = id2;
        assertThrows(ResponseStatusException.class, () -> sut.getRequestsForProject(finalId));
    }

    @Test
    void testGetRequestsForProjectEmptyList() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        while(id2.equals(id1)){
            id2 = UUID.randomUUID();
        }
        Project p = new Project("title", "desc", false);
        p.setProjectId(id1);
        p.setRequests(new ArrayList<>());
        when(projectRepository.findAll()).thenReturn(List.of(p));
        assertEquals(sut.getRequestsForProject(id1), new ArrayList<>());
    }

    @Test
    void testGetRequestsForProjectNonEmptyList() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        while(id2.equals(id1)){
            id2 = UUID.randomUUID();
        }
        Project p = new Project("title", "desc", false);
        p.setProjectId(id1);
        Request r = new Request("title", "description", false, new Account(), new Project());
        p.setRequests(List.of(r));
        when(projectRepository.findAll()).thenReturn(List.of(p));
        assertEquals(sut.getRequestsForProject(id1), List.of(r));
    }

    @Test
    void testDeleteIdNull() {
        assertThrows(ResponseStatusException.class, () -> sut.deleteRequest(null));
    }

    @Test
    void testDeleteNotFound() {
        UUID id1 = UUID.randomUUID();
        when(requestRepository.findById(id1)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> sut.deleteRequest(id1));
    }

    @Test
    void testDeleteOk() {
        UUID id1 = UUID.randomUUID();
        Request r = new Request("title", "description", false, new Account(), new Project());
        when(requestRepository.findById(id1)).thenReturn(Optional.of(r));
        sut.deleteRequest(id1);
        verify(requestRepository).deleteByRequestId(any());
    }

    @Test
    void testAcceptRequestNotFound() {
        UUID id1 = UUID.randomUUID();
        when(requestRepository.findById(id1)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> sut.acceptRequest(id1));
    }

    @Test
    void testAcceptRequestOk() throws Exception {
        RequestTagProject r1add = new RequestTagProject(new Request(), new Tag(), false);
        RequestTagProject r1rem = new RequestTagProject(new Request(), new Tag(), true);

        RequestCollaboratorsProjects r2add = new RequestCollaboratorsProjects( new Collaborator(),new Request(), false);
        RequestCollaboratorsProjects r2rem = new RequestCollaboratorsProjects( new Collaborator(),new Request(), true);

        RequestMediaProject r3add = new RequestMediaProject(new Request(), new Media(), false);
        RequestMediaProject r3rem = new RequestMediaProject(new Request(), new Media(), true);

        RequestLinkProject r4add = new RequestLinkProject(new Request(), new Link(), false);
        RequestLinkProject r4rem = new RequestLinkProject(new Request(), new Link(), true);

        Request r = new Request();
        r.setRequestTagProjects(List.of(r1add, r1rem));
        r.setRequestCollaboratorsProjects(List.of(r2add, r2rem));
        r.setRequestMediaProjects(List.of(r3add, r3rem));
        r.setRequestLinkProjects(List.of(r4add, r4rem));

        Project p = new Project();
        r.setProject(p);

        when(requestRepository.findById(any())).thenReturn(Optional.of(r));
        sut.acceptRequest(UUID.randomUUID());

        verify(projectRepository).save(p);

        verify(tagToProjectRepository).findAllByProjectProjectIdAndTagTagId(any(), any());
        verify(tagToProjectRepository).deleteAll(any());
        verify(tagToProjectRepository).save(any());

        verify(projectsToCollaboratorsRepository).findAllByProjectProjectIdAndCollaboratorCollaboratorId(any(), any());
        verify(projectsToCollaboratorsRepository).deleteAll(any());
        verify(projectsToCollaboratorsRepository).save(any());

        verify(mediaRepository).delete(any());
        verify(mediaRepository).save(any());

        verify(linkRepository).delete(any());
        verify(linkRepository).save(any());

        verify(requestRepository).deleteByRequestId(any());


    }

    @Test
    void testGetRequestsForIdOk () {
        Request r = new Request();
        when(requestRepository.findById(any())).thenReturn(Optional.of(r));
        assertEquals(sut.getRequestForId(UUID.randomUUID()), r);
    }

    @Test
    void testGetRequestsForIdNotFound () {
        when(requestRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, ()-> sut.getRequestForId(UUID.randomUUID()));
    }

    @Test
    void testGetRequestsByIdOk () {
        Request r = new Request();
        when(requestRepository.findById(any())).thenReturn(Optional.of(r));
        assertEquals(sut.getRequestById(UUID.randomUUID()), r);
    }

    @Test
    void  testGetRequestByIdNotFound () {
        when(requestRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, ()-> sut.getRequestById(UUID.randomUUID()));
    }


}
