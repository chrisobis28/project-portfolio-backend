package com.team2a.ProjectPortfolio.Controllers;

import com.team2a.ProjectPortfolio.Commons.Request;
import com.team2a.ProjectPortfolio.Exceptions.NotFoundException;
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

}