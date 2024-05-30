package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import com.team2a.ProjectPortfolio.dto.LoginUserRequest;
import com.team2a.ProjectPortfolio.dto.RegisterUserRequest;
import com.team2a.ProjectPortfolio.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthenticationService {

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenUtil jwtTokenUtil;

    /**
     * Constructor for the Authentication Service
     * @param accountRepository - the Account Repository
     * @param passwordEncoder - the Password Encoder
     * @param jwtTokenUtil - the JWT Token Util
     */
    @Autowired
    public AuthenticationService(AccountRepository accountRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtTokenUtil jwtTokenUtil) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /**
     * Registers a user
     * @param registerUserRequest - the Register User Request
     */
    public void registerUser (RegisterUserRequest registerUserRequest) {
        if (accountRepository.existsById(registerUserRequest.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists.");
        }
        Account newAccount = new Account();
        newAccount.setUsername(registerUserRequest.getUsername());
        newAccount.setPassword(passwordEncoder.encode(registerUserRequest.getPassword()));
        newAccount.setName(registerUserRequest.getName());
        newAccount.setIsAdministrator(false);
        newAccount.setIsPM(false);
        accountRepository.save(newAccount);
    }

    /**
     * Authenticates a user
     * @param loginUserRequest - the Login User Request
     * @return - the JWT token
     */
    public String authenticate (LoginUserRequest loginUserRequest) {
        Account account = accountRepository.findById(loginUserRequest.getUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username or password is incorrect."));

        if (!passwordEncoder.matches(loginUserRequest.getPassword(), account.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username or password is incorrect.");
        }

        return jwtTokenUtil.generateToken(loginUserRequest.getUsername());
    }
}
