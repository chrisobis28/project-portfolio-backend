package com.team2a.ProjectPortfolio.Controllers;

import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.AuthenticationService;
import com.team2a.ProjectPortfolio.WebSocket.AccountWebSocketHandler;
import com.team2a.ProjectPortfolio.dto.LoginUserRequest;
import com.team2a.ProjectPortfolio.dto.RegisterUserRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(Routes.AUTHENTICATION)
@CrossOrigin("http://localhost:4200")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    private final AccountWebSocketHandler accountWebSocketHandler;

    private final String cookieName = "auth-cookie";

    @Value("${jwt.expiration}")
    private int expiration;

    /**
     * Constructor for the Authentication Controller
     * @param authenticationService - the service for authentication
     * @param accountWebSocketHandler - the handler for the web socket
     */
    @Autowired
    public AuthenticationController(AuthenticationService authenticationService,
                                    AccountWebSocketHandler accountWebSocketHandler) {
        this.authenticationService = authenticationService;
        this.accountWebSocketHandler = accountWebSocketHandler;
    }

    /**
     * Registers an Account
     * @param registerUserRequest - the request to register
     * @return - HTTP.CREATED if successful
     */
    @PostMapping("/register")
    public ResponseEntity<Void> createAccount (@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        authenticationService.registerUser(registerUserRequest);
        accountWebSocketHandler.broadcast("add " + registerUserRequest.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Logs in an Account
     * @param loginUserRequest - the request to log in
     * @param httpServletRequest - the request given
     * @param response - the response given
     * @return - the token of the Account
     */
    @PostMapping("/login")
    public ResponseEntity<String> login (@Valid @RequestBody LoginUserRequest loginUserRequest,
                                         HttpServletRequest httpServletRequest,
                                         HttpServletResponse response) {
        if(httpServletRequest.getCookies()!=null &&
            !Arrays.stream(httpServletRequest.getCookies())
                .filter(x -> x.getName().equals("auth-cookie")).toList()
                .isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Already logged in.");
        }
        String token = authenticationService.authenticate(loginUserRequest);
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(expiration / 1000);
        response.addCookie(cookie);

        Instant now = Instant.now();
        Instant expirationInstant = now.plus(60, ChronoUnit.MINUTES);
        Date expirationDate = Date.from(expirationInstant);

        return ResponseEntity.ok(expirationDate.toString());
    }

    /**
     * the Logout into an Account method
     * @param response - the response given which contains and invalid cookie
     * @return - the status of the logout
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout (HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok("Logged out successfully");
    }

    /**
     * Gets the role of an Account
     * @param username - the username of the Account
     * @return - the role of the Account
     */
    @GetMapping("/role/{username}")
    public ResponseEntity<String> getRole (@PathVariable String username) {
        return ResponseEntity.ok(authenticationService.getAccountRole(username));
    }
}