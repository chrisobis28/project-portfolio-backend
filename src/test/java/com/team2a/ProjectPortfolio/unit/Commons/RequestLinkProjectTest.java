package com.team2a.ProjectPortfolio.unit.Commons;

import com.team2a.ProjectPortfolio.Commons.RequestLinkProject;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RequestLinkProjectTest {

    @Test
    void testConstructor() {
        UUID id1 = UUID.randomUUID();

        RequestLinkProject rlp = new RequestLinkProject(id1, true);
        assertEquals(rlp.getRequestLinkProjectId(), id1);
        assertTrue(rlp.isRemove());
    }

}