package com.team2a.ProjectPortfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Commons.Request;
import com.team2a.ProjectPortfolio.Commons.Role;
import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.RequestRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc(addFilters=false)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Transactional
public class RequestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID projectId;

    private String accountId;

    private Account account;

    private Project project;

    @BeforeEach
    public void setup() {
        requestRepository.deleteAll();
        projectRepository.deleteAll();
        accountRepository.deleteAll();

        project = new Project("Test Project", "Description", false);
        project = projectRepository.saveAndFlush(project);
        projectId = project.getProjectId();

        account = new Account("username", "name", "password", Role.ROLE_USER);
        account = accountRepository.saveAndFlush(account);
        accountId = account.getUsername();


    }


    @Test
    public void addRequest() throws Exception {
        Project project2 = new Project("Test Project2", "Description2", false);
        project2 = projectRepository.saveAndFlush(project2);
        Account account2 = new Account("username2", "name2", "password2", Role.ROLE_USER);
        account2 = accountRepository.saveAndFlush(account2);
        Request request = new Request("Title2", "Description2", false, account2, project2);
        mockMvc.perform(put("/request/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.newTitle", is("Title2")))
                .andExpect(jsonPath("$.newDescription", is("Description2")))
                .andExpect(jsonPath("$.account.username", is("username2")))
                .andExpect(jsonPath("$.project.projectId", is(project2.getProjectId().toString())));
        assertEquals(1, requestRepository.findAll().size());
        assertEquals(1,accountRepository.findById("username2").get().getRequests().size());
        Request requestForSameProject = new Request("Title3", "Description3", false, account2, project2);
        mockMvc.perform(put("/request/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestForSameProject)))
                .andExpect(status().isConflict());
        Request invalidRequest = new Request("Title3", "Description3", false, account2, null);
        mockMvc.perform(put("/request/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetRequestsForUser() throws Exception {
        mockMvc.perform(put("/request/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new Request("Title2", "Description2", false, account, project))));
        assertEquals(1,accountRepository.findById(accountId).get().getRequests().size());
        mockMvc.perform(get("/request/user/" + accountId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].newTitle", is("Title2")))
                .andExpect(jsonPath("$[0].newDescription", is("Description2")))
                .andExpect(jsonPath("$[0].account.username", is(accountId)))
                .andExpect(jsonPath("$[0].project.projectId", is(projectId.toString())));
    }

}
