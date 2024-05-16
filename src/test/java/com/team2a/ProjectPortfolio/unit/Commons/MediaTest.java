package com.team2a.ProjectPortfolio.unit.Commons;

import com.team2a.ProjectPortfolio.Commons.Media;
import com.team2a.ProjectPortfolio.Commons.Project;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MediaTest {

    @Test
    void testConstructor() {
        Project p = new Project("title", "description", "bibtex", false);
        Media m = new Media(p, "path");
        assertEquals(m.getProject(), p);
        assertEquals(m.getPath(), "path");
    }

}