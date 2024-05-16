package com.team2a.ProjectPortfolio.unit.Commons;

import com.team2a.ProjectPortfolio.Commons.RequestTagProject;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RequestTagProjectTest {

    @Test
    void testConstructor() {
        UUID id1 = UUID.randomUUID();
        RequestTagProject rtp = new RequestTagProject(id1, false);
        assertEquals(rtp.getRequestTagProjectID(), id1);
        assertFalse(rtp.isRemove());
    }

}