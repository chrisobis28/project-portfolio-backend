package com.team2a.ProjectPortfolio.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtTokenUtilTest {

    @InjectMocks
    private JwtTokenUtil jwtTokenUtil;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(jwtTokenUtil, "secret", "mysecret");
        ReflectionTestUtils.setField(jwtTokenUtil, "expiration", 3600L);
    }

    @Test
    public void testGenerateToken() {
        String username = "testuser";

        String token = jwtTokenUtil.generateToken(username);

        assertNotNull(token);
        String extractedUsername = jwtTokenUtil.getUsernameFromToken(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    public void testValidateToken_ValidToken() {
        String username = "testuser";
        String token = jwtTokenUtil.generateToken(username);

        boolean isValid = jwtTokenUtil.validateToken(token, username);

        assertTrue(isValid);
    }

    @Test
    public void testValidateToken_InvalidToken() {
        String username = "testuser";
        String token = jwtTokenUtil.generateToken(username);

        boolean isValid = jwtTokenUtil.validateToken(token, "otheruser");

        assertFalse(isValid);
    }

    @Test
    public void testGetUsernameFromToken() {
        String username = "testuser";
        String token = jwtTokenUtil.generateToken(username);

        String extractedUsername = jwtTokenUtil.getUsernameFromToken(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    public void testGetExpirationDateFromToken() {
        String username = "testuser";
        String token = jwtTokenUtil.generateToken(username);

        Date expirationDate = jwtTokenUtil.getExpirationDateFromToken(token);

        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    public void testGetClaimFromToken() {
        String username = "testuser";
        String token = jwtTokenUtil.generateToken(username);

        Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);

        assertEquals(username, claims.getSubject());
    }

    @Test
    public void testIsTokenExpired_NotExpired() {
        String username = "testuser";
        String token = jwtTokenUtil.generateToken(username);

        boolean isExpired = jwtTokenUtil.isTokenExpired(token);

        assertFalse(isExpired);
    }

}
