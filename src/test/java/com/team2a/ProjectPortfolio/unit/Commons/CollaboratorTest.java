package com.team2a.ProjectPortfolio.unit.Commons;

import com.team2a.ProjectPortfolio.Commons.Collaborator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CollaboratorTest {

    @Test
    void testConstructor() {
        UUID id1 = UUID.randomUUID();
        Collaborator c = new Collaborator("name");
        assertEquals(c.getName(), "name");
        assertEquals(c.getProjectsToCollaborators(), new ArrayList<>());
    }

}