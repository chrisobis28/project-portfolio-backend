package com.team2a.ProjectPortfolio.unit.Controllers;

import com.team2a.ProjectPortfolio.Commons.Request;
import com.team2a.ProjectPortfolio.Controllers.RequestController;
import com.team2a.ProjectPortfolio.CustomExceptions.NotFoundException;
import com.team2a.ProjectPortfolio.Services.RequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class RequestControllerTest {

    @Mock
    private RequestService requestService;

    private RequestController sut;

    @BeforeEach
    void setup() {
        requestService = Mockito.mock(RequestService.class);
        sut = new RequestController(requestService);
    }
    @Test
    void testGetRequestsForUserOk() {

        Request r = new Request(UUID.randomUUID(), "title", "desc",
                "bib", true);
        when(requestService.getRequestsForUser("aa")).thenReturn(List.of(r));

        ResponseEntity<List<Request>> resp = sut.getRequestsForUser("aa");

        assertEquals(resp.getStatusCode(), HttpStatus.OK);
        assertEquals(resp.getBody(), List.of(r));
    }

    @Test
    void testGetRequestsForUserNotFound() {
        when(requestService.getRequestsForUser("aa")).thenThrow(NotFoundException.class);

        ResponseEntity<List<Request>> resp = sut.getRequestsForUser("aa");

        assertEquals(resp.getStatusCode(), HttpStatus.NOT_FOUND);

    }

    @Test
    void testGetRequests () {

        Request r = new Request(UUID.randomUUID(), "title", "description", "bibtex", false);
        when(requestService.getRequests()).thenReturn(List.of(r));

        ResponseEntity<List<Request>> res = sut.getRequests();

        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), List.of(r));
    }

    @Test
    void testGetRequestsForProjectNotFound () {
        UUID id1 = UUID.randomUUID();
        when(requestService.getRequestsForProject(id1)).thenThrow(new NotFoundException());
        ResponseEntity<List<Request>> res = sut.getRequestsForProject(id1);

        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetRequestsForProjectFound () {
        UUID id1 = UUID.randomUUID();
        Request r = new Request(UUID.randomUUID(), "title", "desc",
                "bib", true);
        when(requestService.getRequestsForProject(id1)).thenReturn(List.of(r));
        ResponseEntity<List<Request>> res = sut.getRequestsForProject(id1);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), List.of(r));
    }

}