package com.team2a.ProjectPortfolio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Commons.Role;
import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import com.team2a.ProjectPortfolio.dto.LoginUserRequest;
import com.team2a.ProjectPortfolio.dto.RegisterUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RegisterUserRequest registerUserRequest;

    private LoginUserRequest loginUserRequest;

    @BeforeEach
    public void setUp() {
        accountRepository.deleteAll();
        registerUserRequest = new RegisterUserRequest("username","Password!1","user");
        loginUserRequest = new LoginUserRequest("username","Password!1");
    }

    @Test
    public void testRegisterAccount() throws Exception {
        mockMvc.perform(post("/authentication/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUserRequest)))
                .andExpect(status().isCreated());
        assertEquals(1, accountRepository.count());
        Account account = accountRepository.findById("username")
            .orElseThrow(() -> new AssertionError("Account not found"));
        assertEquals("username", account.getUsername());
        assertTrue(passwordEncoder.matches("Password!1", account.getPassword()));
        assertEquals("user", account.getName());
        assertEquals(Role.ROLE_USER, account.getRole());
    }

    @Test
    public void testLogin() throws Exception {
        mockMvc.perform(post("/authentication/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUserRequest)))
            .andExpect(status().isCreated());
        mockMvc.perform(post("/authentication/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginUserRequest)))
            .andExpect(status().isOk());
    }

}
