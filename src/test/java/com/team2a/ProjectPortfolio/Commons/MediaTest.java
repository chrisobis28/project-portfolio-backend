package com.team2a.ProjectPortfolio.Commons;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MediaTest {

    @Test
    void testConstructor() {
        UUID id1 = UUID.randomUUID();
        Media m = new Media(id1, "path");
        assertEquals(m.getMediaId(), id1);
        assertEquals(m.getPath(), "path");
    }

}