package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Commons.Request;
import com.team2a.ProjectPortfolio.Exceptions.NotFoundException;
import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import com.team2a.ProjectPortfolio.Repositories.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class RequestServiceTest {

    private RequestService sut;

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private RequestRepository requestRepository;

    @BeforeEach
    void setup() {
        sut = new RequestService();
        accountRepository = Mockito.mock(AccountRepository.class);
        requestRepository = Mockito.mock(RequestRepository.class);
        sut.setAccountRepository(accountRepository);
        sut.setRequestRepository(requestRepository);
    }

    @Test
    void testGetRequestsForUserEmptyUsername() {
        assertThrows(NotFoundException.class, () -> {sut.getRequestsForUser(null);});
    }

    @Test
    void testGetRequestForUserUserNotFound() {
        when(accountRepository.findAll()).thenReturn(List.of(new Account("uname",
                "Name", "pw", true, false)));
        assertThrows(NotFoundException.class, () -> {sut.getRequestsForUser("Name");});
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





}