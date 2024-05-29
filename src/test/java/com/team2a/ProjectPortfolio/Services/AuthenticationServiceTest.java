package com.team2a.ProjectPortfolio.Services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import com.team2a.ProjectPortfolio.dto.RegisterUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        accountRepository = Mockito.mock(AccountRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        authenticationService = new AuthenticationService(accountRepository, passwordEncoder);
    }

    @Test
    void testRegisterUserAlreadyExists() {
        when(accountRepository.existsById("username")).thenReturn(true);
        assertThrows(ResponseStatusException.class, () -> authenticationService.registerUser(new RegisterUserRequest("username", "name", "Password!")));
    }

    @Test
    void testRegisterUserSuccess() {
        when(accountRepository.existsById("username")).thenReturn(false);
        RegisterUserRequest request = new RegisterUserRequest("username", "name", "password");
        assertDoesNotThrow(() -> authenticationService.registerUser(request));
    }
}
