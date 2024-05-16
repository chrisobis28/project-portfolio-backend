package com.team2a.ProjectPortfolio.Commons;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RequestCollaboratorsProjectsTest {

    @Test
    void testConstructor() {
        UUID id1 = UUID.randomUUID();
        RequestCollaboratorsProjects rcp = new RequestCollaboratorsProjects(id1, false);
        assertEquals(rcp.getId(), id1);
        assertFalse(rcp.getIsRemove());
    }

}