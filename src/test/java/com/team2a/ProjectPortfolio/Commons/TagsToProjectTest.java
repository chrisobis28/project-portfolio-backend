package com.team2a.ProjectPortfolio.Commons;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TagsToProjectTest {

    @Test
    void testConstructor() {
        UUID id1 = UUID.randomUUID();
        TagsToProject ttp = new TagsToProject(id1);

        assertEquals(ttp.getTagToProjectId(), id1);
    }

}