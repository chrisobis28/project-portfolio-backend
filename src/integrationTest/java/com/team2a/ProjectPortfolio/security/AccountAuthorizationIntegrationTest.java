package com.team2a.ProjectPortfolio.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.Role;
import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectsToAccountsRepository;
import com.team2a.ProjectPortfolio.Routes;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters=false)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class AccountAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SecurityConfigUtils securityConfigUtils;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProjectsToAccountsRepository projectsToAccountsRepository;
    @Autowired
    private ObjectMapper objectMapper;
    private Project project1;

    @BeforeEach
    public void setUp() {
        accountRepository.deleteAll();
        projectRepository.deleteAll();
        projectsToAccountsRepository.deleteAll();
        project1 = new Project("title1", "description1", true);
    }

    @Test
    public void testAddRoleAuthorized() throws Exception {
        securityConfigUtils.setProjectManagerWithUsername("username1");
        accountRepository.save(securityConfigUtils.getAccount());
        mockMvc.perform(post(Routes.PROJECT + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(project1)))
            .andExpect(status().isOk());

        UUID project1Id = projectRepository.findAll().get(0).getProjectId();

        Account accountToBeAdded = new Account("username2", "name2", "password2", Role.ROLE_USER);
        accountRepository.save(accountToBeAdded);

        mockMvc.perform(post(Routes.ACCOUNT + "/" + accountToBeAdded.getUsername() + "/" + project1Id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("CONTENT_CREATOR")))
            .andExpect(status().isOk());
    }

    @Test
    public void testAddRoleUnauthorizedPmButNotInProject() throws Exception {
        securityConfigUtils.setProjectManagerWithUsername("username1");
        accountRepository.save(securityConfigUtils.getAccount());
        mockMvc.perform(post(Routes.PROJECT + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(project1)))
            .andExpect(status().isOk());

        UUID project1Id = projectRepository.findAll().get(0).getProjectId();

        Account accountToBeAdded = new Account("username2", "name2", "password2", Role.ROLE_USER);
        accountRepository.save(accountToBeAdded);

        securityConfigUtils.setProjectManagerWithUsername("username3");
        accountRepository.save(securityConfigUtils.getAccount());
        mockMvc.perform(post(Routes.ACCOUNT + "/" + accountToBeAdded.getUsername() + "/" + project1Id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("CONTENT_CREATOR")))
            .andExpect(status().isForbidden());
    }

    @Test
    public void testUpdateProjectAuthorized() throws Exception {
        securityConfigUtils.setProjectManagerWithUsername("username1");
        accountRepository.save(securityConfigUtils.getAccount());
        mockMvc.perform(post(Routes.PROJECT + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(project1)))
            .andExpect(status().isOk());

        UUID project1Id = projectRepository.findAll().get(0).getProjectId();

        Account accountToBeAdded = new Account("username2", "name2", "password2", Role.ROLE_USER);
        accountRepository.save(accountToBeAdded);

        mockMvc.perform(post(Routes.ACCOUNT + "/" + accountToBeAdded.getUsername() + "/" + project1Id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("EDITOR")))
            .andExpect(status().isOk());

        securityConfigUtils.setCurrentAccount(accountToBeAdded);
        Project project2 = new Project("title2", "description2", false);
        mockMvc.perform(put(Routes.PROJECT + "/" + project1Id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(project2)))
            .andExpect(status().isOk());
    }
}
