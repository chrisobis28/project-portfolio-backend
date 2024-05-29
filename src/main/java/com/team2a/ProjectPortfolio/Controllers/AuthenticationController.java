package com.team2a.ProjectPortfolio.Controllers;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.AuthenticationService;
import com.team2a.ProjectPortfolio.dto.LoginUserRequest;
import com.team2a.ProjectPortfolio.dto.RegisterUserRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.AUTHENTICATION)
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Constructor for the Authentication Controller
     * @param authenticationService - the Authentication Service
     */
    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Registers an Account
     * @param registerUserRequest - the request to register
     * @return - HTTP.CREATED if successful
     */
    @PostMapping("/register")
    public ResponseEntity<Void> createAccount (@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        authenticationService.registerUser(registerUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

//    /**
//     * Logs in an Account
//     * @param loginUserRequest - the request to log in
//     * @return - the token of the Account
//     */
//    @PostMapping("/login")
//    public ResponseEntity<String> login(@Valid @RequestBody LoginUserRequest loginUserRequest) {
//        String token = authenticationService.authenticate(loginUserRequest);
//        return ResponseEntity.ok(token);
//    }
}
