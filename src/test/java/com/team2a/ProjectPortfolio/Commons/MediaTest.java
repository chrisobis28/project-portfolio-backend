package com.team2a.ProjectPortfolio.Commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MediaTest {

    @Test
    void testConstructor() {
        Media m = new Media("name", "path");
        assertEquals(m.getName(), "name");
        assertEquals(m.getPath(), "path");
    }

}