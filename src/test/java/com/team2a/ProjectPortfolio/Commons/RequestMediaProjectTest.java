package com.team2a.ProjectPortfolio.Commons;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RequestMediaProjectTest {

    @Test
    void testConstructor() {
        UUID id1 = UUID.randomUUID();

        RequestMediaProject r = new RequestMediaProject(id1, true);
        assertEquals(r.getRequestMediaProjectId(), id1);
        assertTrue(r.getIsRemove());
    }

}