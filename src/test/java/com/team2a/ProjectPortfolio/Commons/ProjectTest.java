package com.team2a.ProjectPortfolio.Commons;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    @Test
    void testConstructor() {
        UUID id1 = UUID.randomUUID();
        Project p = new Project("title", "description", "bibtex", true);
        assertEquals(p.getTitle(), "title");
        assertEquals(p.getDescription(), "description");
        assertEquals(p.getBibtex(), "bibtex");
        assertEquals(p.getArchived(), true);
        assertEquals(p.getMedia(), new ArrayList<>());
    }

}