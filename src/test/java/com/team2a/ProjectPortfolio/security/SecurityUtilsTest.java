package com.team2a.ProjectPortfolio.security;

import com.team2a.ProjectPortfolio.Commons.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityUtilsTest {

    private SecurityUtils securityUtils;

    @BeforeEach
    void setUp() {
        securityUtils = new SecurityUtils();
    }

    @Test
    void getCurrentUser() {
        Account mockAccount = mock(Account.class);
        Authentication authentication = new UsernamePasswordAuthenticationToken(mockAccount, null);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Account currentUser = securityUtils.getCurrentUser();
        assertEquals(mockAccount, currentUser);
    }
}
