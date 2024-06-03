package com.team2a.ProjectPortfolio.security;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JwtRequestFilterTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ApplicationContext context;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Account account;
    private final List<String> publicEndpoints = List.of("/public");


    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(jwtRequestFilter, "publicEndpoints", publicEndpoints);
        when(request.getMethod()).thenReturn("POST");

    }


    @Test
    public void testDoFilterInternal_PublicEndpoint() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("GET");
        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtTokenUtil.getUsernameFromToken("validToken")).thenReturn("username");
        when(account.getUsername()).thenReturn("username");
        when(accountRepository.findById("username")).thenReturn(Optional.of(account));
        when(jwtTokenUtil.validateToken("validToken", "username")).thenReturn(true);
        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_ExpiredToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer expiredToken");
        when(jwtTokenUtil.getUsernameFromToken("expiredToken")).thenThrow(ExpiredJwtException.class);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");
        when(jwtTokenUtil.getUsernameFromToken("invalidToken")).thenReturn("username");
        when(account.getUsername()).thenReturn("username");
        when(accountRepository.findById("username")).thenReturn(Optional.of(account));
        when(jwtTokenUtil.validateToken("invalidToken", "username")).thenReturn(false);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testDoFilterInternal_NoToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
