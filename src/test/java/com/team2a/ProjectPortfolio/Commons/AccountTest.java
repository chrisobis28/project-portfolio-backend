package com.team2a.ProjectPortfolio.Commons;

import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account("uname", "name", "pw", Role.ROLE_USER);
    }


    @Test
    void testAccount() {
        Account a = new Account("uname", "name", "pw", Role.ROLE_USER);
        assertEquals(a.getUsername(), "uname");
        assertEquals(a.getName(), "name");
        assertEquals(a.getPassword(), "pw");
        assertEquals(Role.ROLE_USER, a.getRole());
        assertEquals(a.getProjectsToAccounts(), new ArrayList<>());
    }

    @Test
    void testGetAuthorities() {
        Collection<? extends GrantedAuthority> authorities = account.getAuthorities();
        assertEquals(new SimpleGrantedAuthority(Role.ROLE_USER.toString()),authorities.stream().toList().get(authorities.size()-1));

        account.setRole(Role.ROLE_PM);
        authorities = account.getAuthorities();
        assertEquals(new SimpleGrantedAuthority(Role.ROLE_PM.toString()),authorities.stream().toList().get(authorities.size()-1));
    }

    @Test
    void testUserDetailsMethods() {
        assertTrue(account.isAccountNonExpired());
        assertTrue(account.isAccountNonLocked());
        assertTrue(account.isCredentialsNonExpired());
        assertTrue(account.isEnabled());
    }

}