package com.team2a.ProjectPortfolio.unit.Commons;

import com.team2a.ProjectPortfolio.Commons.Account;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void testAccount() {
        Account a = new Account("uname", "name", "pw", true, false);
        assertEquals(a.getUsername(), "uname");
        assertEquals(a.getName(), "name");
        assertEquals(a.getPassword(), "pw");
        assertEquals(a.getIsPM(), false);
        assertEquals(a.getIsAdministrator(), true);
        assertEquals(a.getProjectsToAccounts(), new ArrayList<>());
    }

}