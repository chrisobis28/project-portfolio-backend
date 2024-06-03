package com.team2a.ProjectPortfolio.Commons;

import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import org.springframework.security.core.GrantedAuthority;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account("uname", "name", "pw", true, false);
    }


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

    @Test
    void testGetAuthorities() {
        Collection<? extends GrantedAuthority> authorities = account.getAuthorities();
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        assertFalse(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_PM")));
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));

        account.setIsAdministrator(false);
        account.setIsPM(true);
        authorities = account.getAuthorities();
        assertFalse(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_PM")));
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testUserDetailsMethods() {
        assertTrue(account.isAccountNonExpired());
        assertTrue(account.isAccountNonLocked());
        assertTrue(account.isCredentialsNonExpired());
        assertTrue(account.isEnabled());
    }

}