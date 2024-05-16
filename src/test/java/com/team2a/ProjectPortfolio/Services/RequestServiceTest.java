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
        assertThrows(NotFoundException.class, () -> sut.getRequestsForUser(null));
    }

    @Test
    void testGetRequestForUserUserNotFound() {
        when(accountRepository.findAll()).thenReturn(List.of(new Account("uname",
                "Name", "pw", true, false)));
        assertThrows(NotFoundException.class, () -> sut.getRequestsForUser("Name"));
    }

    @Test
    void testGetRequestForUserOk() {
        Account a = new Account("uname", "Name",
                "pw", true, false);

        Request r = new Request(UUID.randomUUID(), "title", "desc",
                "bib", true);
        a.setRequests(List.of(r));
        when(accountRepository.findAll()).thenReturn(List.of(a));
        assertEquals(sut.getRequestsForUser("uname"), List.of(r));
    }

    @Test
    void testGetRequests() {
        Request r = new Request(UUID.randomUUID(), "title", "description", "bibtex", false);
        when(requestRepository.findAll()).thenReturn(List.of(r));
        assertEquals(sut.getRequests(), List.of(r));
    }

    @Test
    void testAddRequestProjectNotFound() {
        UUID id1 = UUID.randomUUID();
        when(projectRepository.findById(id1)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> sut.addRequest(new Request(), id1));
    }

    @Test
    void testAddRequestSameRequestFound() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        while(id1.equals(id2))
            id2 = UUID.randomUUID();

        while(id1.equals(projectId) || id2.equals(projectId))
            projectId = UUID.randomUUID();

        Request r1 = new Request(id1, "title", "description", "bibtex", false);
        Request r2 = new Request(id2, "title", "description", "bibtex", false);

        r1.setProject(new Project());

        when(requestRepository.findAll()).thenReturn(List.of(r1));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(new Project()));


        assertEquals(sut.addRequest(r2, projectId).getRequestId(), id1);

    }

    @Test
    void testAddRequestOk() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        while(id1.equals(id2))
            id2 = UUID.randomUUID();

        while(id1.equals(projectId) || id2.equals(projectId))
            projectId = UUID.randomUUID();

        Request r1 = new Request(id1, "different_title", "description", "bibtex", false);
        Request r2 = new Request(id2, "title", "description", "bibtex", false);

        r1.setProject(new Project("othername", "desc", "bib", true));

        when(requestRepository.findAll()).thenReturn(List.of(r1));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(new Project("name", "description", "bibtex", false)));

        Request r = sut.addRequest(r2, projectId);
        verify(requestRepository).save(r2);

        assertEquals(r.getProject(), new Project("name", "description", "bibtex", false));
        r2.setProject(new Project("name", "description", "bibtex", false));
        assertEquals(r, r2);

    }

    @Test
    void testGetRequestsForProjectNull () {
        assertThrows(NotFoundException.class, () -> sut.getRequestsForProject(null));
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
        assertThrows(NotFoundException.class, () -> sut.getRequestsForProject(finalId));
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
        Request r = new Request(UUID.randomUUID(), "title", "description", "bibtex", false);

        p.setRequests(List.of(r));
        when(projectRepository.findAll()).thenReturn(List.of(p));
        assertEquals(sut.getRequestsForProject(id1), List.of(r));
    }

    @Test
    void testDeleteIdNull () {
        assertThrows(NotFoundException.class, () -> sut.deleteRequest(null));
    }

    @Test
    void testDeleteNotFound () {
        UUID id1 = UUID.randomUUID();
        when(requestRepository.findById(id1)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> sut.deleteRequest(id1));
    }

    @Test
    void testDeleteOk () {
        UUID id1 = UUID.randomUUID();

        Request r = new Request(id1, "newTitle", "newDesc", "newBib", false);
        when(requestRepository.findById(id1)).thenReturn(Optional.of(r));
        sut.deleteRequest(id1);
        verify(requestRepository).delete(r);
    }



}