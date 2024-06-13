package com.team2a.ProjectPortfolio.security;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AccountRepository accountRepository;

    @Value("#{'${public.endpoints}'.split(',')}")
    private List<String> publicEndpoints;

    /**
     * If the request is not a public endpoint,
     * the JWT token is validated and the user is authenticated
     *
     * @param request     - the HTTP Servlet Request
     * @param response    - the HTTP Servlet Response
     * @param filterChain - the Filter Chain
     * @throws ServletException - if a Servlet Exception occurs
     * @throws IOException      - if an IO Exception occurs
     */
    @Override
    protected void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        final String requestURI = request.getRequestURI();

        boolean isPublicEndpoint =
            publicEndpoints.stream()
                .anyMatch(endpoint -> new AntPathMatcher().match(endpoint, requestURI));

        if (!isPublicEndpoint) {
            if(request.getCookies()==null){
                logger.warn("Cookies are null");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            final String jwtToken = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("auth-cookie"))
                .findFirst().map(Cookie::getValue)
                .orElse(null);

            String username;

            if (jwtToken != null) {
                try {
                    username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                } catch (ExpiredJwtException e) {
                    logger.warn("JWT Token has expired");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                } catch (Exception e) {
                    logger.warn("Something went wrong: " + e.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            } else {
                logger.warn("Token is null");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Account account;
            try {
                account = accountRepository.findById(username).get();
            } catch (NoSuchElementException e) {
                logger.warn(e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            if (jwtTokenUtil.validateToken(jwtToken, account.getUsername())) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(
                    account, null, account.getAuthorities());
                logger.info(account.getAuthorities());
                usernamePasswordAuthenticationToken
                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                logger.info("JTW Token is valid");
                filterChain.doFilter(request, response);
            } else {
                logger.warn("JWT Token is not valid");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else{
            if(!new AntPathMatcher().match("/h2-console/**", requestURI))logger.info("Public endpoint");
            filterChain.doFilter(request, response);
        }
    }
}
