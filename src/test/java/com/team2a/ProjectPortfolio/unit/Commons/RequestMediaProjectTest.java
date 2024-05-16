package com.team2a.ProjectPortfolio.unit.Commons;

import com.team2a.ProjectPortfolio.Commons.RequestMediaProject;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RequestMediaProjectTest {

    @Test
    void testConstructor() {
        UUID id1 = UUID.randomUUID();

        RequestMediaProject r = new RequestMediaProject(id1, true);
        assertEquals(r.getRequestMediaProjectId(), id1);
        assertTrue(r.isRemove());
    }

}