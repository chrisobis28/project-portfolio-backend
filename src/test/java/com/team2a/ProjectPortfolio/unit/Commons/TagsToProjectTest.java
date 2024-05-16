package com.team2a.ProjectPortfolio.unit.Commons;

import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.Tag;
import com.team2a.ProjectPortfolio.Commons.TagsToProject;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TagsToProjectTest {

    @Test
    void testConstructor() {
        UUID id1 = UUID.randomUUID();
        Tag tag = new Tag(id1, "1", "blue", null, null);
        Project p = new Project("title", "description", "bibtex", true);
        TagsToProject ttp = new TagsToProject(tag, p);
        assertEquals(tag, ttp.getTag());
        assertEquals(p, ttp.getProject());
    }
}