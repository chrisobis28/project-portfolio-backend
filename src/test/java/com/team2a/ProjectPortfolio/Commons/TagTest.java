package com.team2a.ProjectPortfolio.Commons;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TagTest {

    @Test
    void testConstructor() {
        Tag t = new Tag("name", "color");
        assertEquals(t.getName(), "name");
        assertEquals(t.getColor(), "color");
        assertEquals(t.getTagsToProjects(), new ArrayList<>());
        assertEquals(t.getRequestTagProjects(), new ArrayList<>());
    }

}