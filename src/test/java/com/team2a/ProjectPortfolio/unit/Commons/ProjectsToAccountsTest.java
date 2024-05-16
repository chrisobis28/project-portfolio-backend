package com.team2a.ProjectPortfolio.unit.Commons;

import com.team2a.ProjectPortfolio.Commons.ProjectsToAccounts;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProjectsToAccountsTest {

    @Test
    void testConstructor() {
        UUID id1 = UUID.randomUUID();
        ProjectsToAccounts pta = new ProjectsToAccounts(id1, "role");
        assertEquals(id1, pta.getPtaId());
        assertEquals("role", pta.getRole());
    }

}