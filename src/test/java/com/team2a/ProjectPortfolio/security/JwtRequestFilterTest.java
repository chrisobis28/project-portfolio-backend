package com.team2a.ProjectPortfolio.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JwtRequestFilterTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Account account;
    private final List<String> publicEndpoints = List.of("/public", "/h2-console/**");


    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(jwtRequestFilter, "publicEndpoints", publicEndpoints);
    }


    @Test
    public void testDoFilterInternal_PublicEndpoint() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/public");
        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("auth-cookie", "validToken")});
        when(jwtTokenUtil.getUsernameFromToken("validToken")).thenReturn("username");
        when(account.getUsername()).thenReturn("username");
        when(accountRepository.findById("username")).thenReturn(Optional.of(account));
        when(jwtTokenUtil.validateToken("validToken", "username")).thenReturn(true);
        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_ExpiredToken() throws ServletException, IOException {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("auth-cookie", "expiredToken")});
        when(jwtTokenUtil.getUsernameFromToken("expiredToken")).thenThrow(ExpiredJwtException.class);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        when(jwtTokenUtil.getUsernameFromToken("invalidToken")).thenReturn("username");
        when(account.getUsername()).thenReturn("username");
        when(accountRepository.findById("username")).thenReturn(Optional.of(account));
        when(jwtTokenUtil.validateToken("invalidToken", "username")).thenReturn(false);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("auth-cookie", "invalidToken")});

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testDoFilterInternal_NoToken() throws ServletException, IOException {
        when(request.getCookies()).thenReturn(null);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testDoFilterInternal_NoSuchElementException() throws ServletException, IOException {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("auth-cookie", "validToken")});
        when(jwtTokenUtil.getUsernameFromToken("validToken")).thenReturn("username");
        when(accountRepository.findById("username")).thenThrow(NoSuchElementException.class);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testDoFilterInternal_NullToken() throws ServletException, IOException {
        when(request.getCookies()).thenReturn(null);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testDoFilterInternal_NoAuthCookie() throws ServletException, IOException {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("another-cookie", "value")});

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testDoFilterInternal_InvalidJwtToken() throws ServletException, IOException {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("auth-cookie", "invalidToken")});
        when(jwtTokenUtil.getUsernameFromToken("invalidToken")).thenThrow(new RuntimeException("Invalid token"));

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testDoFilterInternal_AdditionalPublicEndpoint() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/another-public-endpoint");
        ReflectionTestUtils.setField(jwtRequestFilter, "publicEndpoints", List.of("/another-public-endpoint"));
        jwtRequestFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_ValidTokenWithAuthorities() throws ServletException, IOException {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("auth-cookie", "validToken")});
        when(jwtTokenUtil.getUsernameFromToken("validToken")).thenReturn("username");
        when(account.getUsername()).thenReturn("username");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER"))).when(account).getAuthorities();
        when(accountRepository.findById("username")).thenReturn(Optional.of(account));
        when(jwtTokenUtil.validateToken("validToken", "username")).thenReturn(true);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_H2ConsolePublicEndpoint() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/h2-console/something");

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }



}
