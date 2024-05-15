package com.team2a.ProjectPortfolio.Commons;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TagTest {

    @Test
    void testConstructor() {
        UUID id1 = UUID.randomUUID();
        Tag t = new Tag(id1, "name", "color", new ArrayList<>(), new ArrayList<>());
        assertEquals(t.getTagId(), id1);
        assertEquals(t.getName(), "name");
        assertEquals(t.getColor(), "color");
        assertEquals(t.getTagsToProjects(), new ArrayList<>());
        assertEquals(t.getRequestTagProjects(), new ArrayList<>());
    }

}