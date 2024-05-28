package com.team2a.ProjectPortfolio.Commons;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TagsToProjectTest {

    @Test
    void testConstructor() {
        UUID id1 = UUID.randomUUID();
        Tag tag = new Tag("1", "blue");
        Project p = new Project("title", "description", true);
        TagsToProject ttp = new TagsToProject(tag, p);
        assertEquals(tag, ttp.getTag());
        assertEquals(p, ttp.getProject());
    }
}