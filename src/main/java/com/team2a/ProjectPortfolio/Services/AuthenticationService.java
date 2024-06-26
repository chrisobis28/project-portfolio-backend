package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Commons.Role;
import com.team2a.ProjectPortfolio.Commons.Collaborator;
import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import com.team2a.ProjectPortfolio.Repositories.CollaboratorRepository;
import com.team2a.ProjectPortfolio.dto.LoginUserRequest;
import com.team2a.ProjectPortfolio.dto.RegisterUserRequest;
import com.team2a.ProjectPortfolio.security.JwtTokenUtil;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthenticationService {

    private final AccountRepository accountRepository;

    private final CollaboratorRepository collaboratorRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenUtil jwtTokenUtil;

    /**
     * Constructor for the AuthenticationService
     * @param accountRepository - the Account Repository
     * @param collaboratorRepository - the Collaborator Repository
     * @param passwordEncoder - the Password Encoder
     * @param jwtTokenUtil - the JWT Token Util
     */
    @Autowired
    public AuthenticationService(AccountRepository accountRepository,
                                 CollaboratorRepository collaboratorRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtTokenUtil jwtTokenUtil) {
        this.accountRepository = accountRepository;
        this.collaboratorRepository = collaboratorRepository;
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
        newAccount.setRole(Role.ROLE_USER);
        Optional<Collaborator> collaborator = collaboratorRepository.findByName(registerUserRequest.getName());

        // If the collaborator does not exist, create a new collaborator,
        // otherwise, do nothing
        // in the future there should be a way to allow somebody to link up their
        // account with an existing collaborator, but that would require additional security

        if  (collaborator.isEmpty()){
            Collaborator newCollaborator = new Collaborator();
            newCollaborator.setName(registerUserRequest.getName());
            collaboratorRepository.save(newCollaborator);
        }
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

    /**
     * Gets the role of an account
     * @param username - the username of the account
     * @return - the role of the account or "ROLE_VISITOR" if the account does not exist
     */
    public String getAccountRole (String username){
        Optional<Account> a = accountRepository.findById(username);
        if(a.isEmpty()){
            return "ROLE_VISITOR";
        }
        Account account = a.get();
        return account.getRole().toString();
    }
}
