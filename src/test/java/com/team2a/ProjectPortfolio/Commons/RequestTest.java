package com.team2a.ProjectPortfolio.Commons;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RequestTest {

    @Test
    void testConstructor() {
        UUID id1 = UUID.randomUUID();
        Request r = new Request(id1, "newTitle", "newDescription", "newBibtex", true);
        assertEquals(r.getRequestId(), id1);
        assertEquals(r.getNewTitle(), "newTitle");
        assertEquals(r.getNewDescription(), "newDescription");
        assertEquals(r.getNewBibtex(), "newBibtex");
        assertTrue(r.isCounterOffer());
    }

}