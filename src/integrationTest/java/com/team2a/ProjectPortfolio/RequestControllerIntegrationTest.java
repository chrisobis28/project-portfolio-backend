package com.team2a.ProjectPortfolio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Commons.Request;
import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
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

    @BeforeEach
    public void setup() {
        requestRepository.deleteAll();
        projectRepository.deleteAll();
        accountRepository.deleteAll();

        Project project = new Project("Test Project", "Description", "Bibtex", false);
        project = projectRepository.saveAndFlush(project);
        projectId = project.getProjectId();

        Account account = new Account("username", "name", "password", false, false);
        account = accountRepository.saveAndFlush(account);
        accountId = account.getUsername();

        Request request = new Request("New Title", "newDescription", "Test Request", false, account, project);
        request = requestRepository.saveAndFlush(request);


    }


    @Test
    public void addRequest() throws Exception {
        Project project2 = new Project("Test Project2", "Description2", "Bibtex2", false);
        project2 = projectRepository.saveAndFlush(project2);
        Account account2 = new Account("username2", "name2", "password2", false, false);
        account2 = accountRepository.saveAndFlush(account2);
        Request request = new Request("Title2", "Description2", "Status2", false, account2, project2);
        mockMvc.perform(put("/request/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Title2")))
                .andExpect(jsonPath("$.description", is("Description2")))
                .andExpect(jsonPath("$.status", is("Status2")))
                .andExpect(jsonPath("$.account.username", is("username2")))
                .andExpect(jsonPath("$.project.projectId", is(project2.getProjectId().toString())));
        assertEquals(2, requestRepository.findAll().size());
        assertEquals(1,accountRepository.findById("username").get().getRequests().size());
    }

    @Test
    public void testGetRequestsForUser() throws Exception {
        mockMvc.perform(get("/request/user/" + accountId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("New Title")))
                .andExpect(jsonPath("$[0].description", is("newDescription")))
                .andExpect(jsonPath("$[0].status", is("Test Request")))
                .andExpect(jsonPath("$[0].account.username", is(accountId)))
                .andExpect(jsonPath("$[0].project.projectId", is(projectId.toString())));
    }

}
