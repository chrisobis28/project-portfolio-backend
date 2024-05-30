package com.team2a.ProjectPortfolio.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Generate a JWT token.
     *
     * @param username the username for which the token is generated
     * @return the JWT token
     */
    public String generateToken (String username) {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();
    }

    /**
     * Validate a JWT token.
     *
     * @param token    the JWT token
     * @param username the username to validate against
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken (String token, String username) {
        final String extractedUsername = getUsernameFromToken(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * Get the username from a JWT token.
     *
     * @param token the JWT token
     * @return the username
     */
    public String getUsernameFromToken (String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Get expiration date from a JWT token.
     *
     * @param token the JWT token
     * @return the expiration date
     */
    public Date getExpirationDateFromToken (String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Get a specific claim from a JWT token.
     *
     * @param token the JWT token
     * @param claimsResolver the claims resolver function
     * @param <T> the type of the claim
     * @return the claim
     */
    public <T> T getClaimFromToken (String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Get all claims from a JWT token.
     *
     * @param token the JWT token
     * @return the claims
     */
    Claims getAllClaimsFromToken (String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    /**
     * Check if a JWT token is expired.
     *
     * @param token the JWT token
     * @return true if the token is expired, false otherwise
     */
    boolean isTokenExpired (String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
}
