package com.team2a.ProjectPortfolio.Commons;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProjectsToAccountsTest {

    @Test
    void testConstructor() {
        UUID id1 = UUID.randomUUID();
        Project p = new Project();
        Account a = new Account();
        ProjectsToAccounts pta = new ProjectsToAccounts("role", a, p);
        assertEquals("role", pta.getRole());
        assertEquals(a, pta.getAccount());
        assertEquals(p, pta.getProject());
    }

}