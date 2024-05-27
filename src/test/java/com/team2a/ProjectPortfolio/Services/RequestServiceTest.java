package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.Request;
import com.team2a.ProjectPortfolio.CustomExceptions.NotFoundException;
import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.web.server.ResponseStatusException;

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

    @BeforeEach
    void setup() {
        sut = new RequestService();
        accountRepository = Mockito.mock(AccountRepository.class);
        requestRepository = Mockito.mock(RequestRepository.class);
        projectRepository = Mockito.mock(ProjectRepository.class);
        sut.setAccountRepository(accountRepository);
        sut.setRequestRepository(requestRepository);
        sut.setProjectRepository(projectRepository);
    }

    @Test
    void testGetRequestsForUserEmptyUsername() {
        assertThrows(ResponseStatusException.class, () -> sut.getRequestsForUser(null));
    }

    @Test
    void testGetRequestForUserUserNotFound() {
        when(accountRepository.findAll()).thenReturn(List.of(new Account("uname",
                "Name", "pw", true, false)));
        assertThrows(ResponseStatusException.class, () -> sut.getRequestsForUser("Name"));
    }

    @Test
    void testGetRequestForUserOk() {
        Account a = new Account("uname", "Name",
                "pw", true, false);

        Request r = new Request("title", "desc",
                "bib", true, a, new Project());
        a.setRequests(List.of(r));
        when(accountRepository.findById("uname")).thenReturn(Optional.of(a));
        assertEquals(sut.getRequestsForUser("uname"), List.of(r));
    }

    @Test
    void testGetRequests() {
        Request r = new Request("title", "description", "bibtex", false, new Account(), new Project());
        when(requestRepository.findAll()).thenReturn(List.of(r));
        assertEquals(sut.getRequests(), List.of(r));
    }


    @Test
    void testAddRequestOk() {
        Project p = new Project("title", "desc", "bibtex", false);
        Account a = new Account("uname", "Name",
            "pw", true, false);
        Request r = new Request("title", "description", "bibtex", false, a , p);
        when(projectRepository.findById(p.getProjectId())).thenReturn(Optional.of(p));
        when(accountRepository.findById(a.getUsername())).thenReturn(Optional.of(a));
        when(requestRepository.save(r)).thenReturn(r);
        assertEquals(sut.addRequest(r), r);
    }

    @Test
    void testGetRequestsForProjectNull () {
        assertThrows(ResponseStatusException.class, () -> sut.getRequestsForProject(null));
    }

    @Test
    void testGetRequestsForProjectNotFound () {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        while(id2.equals(id1)){
            id2 = UUID.randomUUID();
        }

        Project p = new Project("title", "desc", "bibtex", false);
        p.setProjectId(id1);
        when(projectRepository.findAll()).thenReturn(List.of(p));
        UUID finalId = id2;
        assertThrows(ResponseStatusException.class, () -> sut.getRequestsForProject(finalId));
    }

    @Test
    void testGetRequestsForProjectEmptyList () {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        while(id2.equals(id1)){
            id2 = UUID.randomUUID();
        }

        Project p = new Project("title", "desc", "bibtex", false);
        p.setProjectId(id1);
        p.setRequests(new ArrayList<>());
        when(projectRepository.findAll()).thenReturn(List.of(p));
        assertEquals(sut.getRequestsForProject(id1), new ArrayList<>());
    }

    @Test
    void testGetRequestsForProjectNonEmptyList () {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        while(id2.equals(id1)){
            id2 = UUID.randomUUID();
        }

        Project p = new Project("title", "desc", "bibtex", false);
        p.setProjectId(id1);
        Request r = new Request("title", "description", "bibtex", false, new Account(), new Project());

        p.setRequests(List.of(r));
        when(projectRepository.findAll()).thenReturn(List.of(p));
        assertEquals(sut.getRequestsForProject(id1), List.of(r));
    }

    @Test
    void testDeleteIdNull () {
        assertThrows(ResponseStatusException.class, () -> sut.deleteRequest(null));
    }

    @Test
    void testDeleteNotFound () {
        UUID id1 = UUID.randomUUID();
        when(requestRepository.findById(id1)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> sut.deleteRequest(id1));
    }

    @Test
    void testDeleteOk () {
        UUID id1 = UUID.randomUUID();

        Request r = new Request("title", "description", "bibtex", false, new Account(), new Project());
        when(requestRepository.findById(id1)).thenReturn(Optional.of(r));
        sut.deleteRequest(id1);
        verify(requestRepository).delete(r);
    }



}