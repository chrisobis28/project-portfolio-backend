package com.team2a.ProjectPortfolio.Commons;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProjectsToCollaboratorsTest {

    @Test
    void testConstructor() {
        UUID id1 = UUID.randomUUID();
        ProjectsToCollaborators ptc = new ProjectsToCollaborators(id1);
        assertEquals(ptc.getPtcId(), id1);
    }

}