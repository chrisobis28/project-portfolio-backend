package com.team2a.ProjectPortfolio.Services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import com.team2a.ProjectPortfolio.Repositories.CollaboratorRepository;
import com.team2a.ProjectPortfolio.dto.LoginUserRequest;
import com.team2a.ProjectPortfolio.dto.RegisterUserRequest;
import com.team2a.ProjectPortfolio.security.JwtTokenUtil;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CollaboratorRepository collaboratorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private AuthenticationService authenticationService;


    @Test
    void testRegisterUserAlreadyExists() {
        when(accountRepository.existsById("username")).thenReturn(true);
        assertThrows(ResponseStatusException.class, () -> authenticationService.registerUser(new RegisterUserRequest("username", "name", "Password!")));
    }

    @Test
    void testRegisterUserSuccess() {
        when(accountRepository.existsById("username")).thenReturn(false);
        when(collaboratorRepository.findByName("username")).thenReturn(Optional.empty());
        RegisterUserRequest request = new RegisterUserRequest("username", "name", "password");
        assertDoesNotThrow(() -> authenticationService.registerUser(request));
    }

    @Test
    void testLoginUserUsernameIncorrect() {
        LoginUserRequest request = new LoginUserRequest("username", "password");
        when(accountRepository.findById("username")).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> authenticationService.authenticate(request));
    }

    @Test
    void testLoginUserPasswordIncorrect() {
        LoginUserRequest request = new LoginUserRequest("username", "passwordIncorrect");
        when(accountRepository.findById("username")).thenReturn(Optional.of(new Account("username", "name", "password", false, false)));
        when(passwordEncoder.matches("passwordIncorrect", "password")).thenReturn(false);
        assertThrows(ResponseStatusException.class, () -> authenticationService.authenticate(request));
    }

    @Test
    void testLoginUserSuccess() {
        LoginUserRequest request = new LoginUserRequest("username", "password");
        when(accountRepository.findById("username")).thenReturn(Optional.of(new Account("username", "name", "password", false, false)));
        when(passwordEncoder.matches("password", "password")).thenReturn(true);
        when(jwtTokenUtil.generateToken("username")).thenReturn("token");
        assertEquals("token", authenticationService.authenticate(request));
    }
}
