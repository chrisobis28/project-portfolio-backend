package com.team2a.ProjectPortfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.Request;
import com.team2a.ProjectPortfolio.Commons.Tag;
import com.team2a.ProjectPortfolio.Commons.TagsToProject;
import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.RequestRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
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

        Account account = new Account("username", "password", "password", false, false);
        account = accountRepository.saveAndFlush(account);
        accountId = account.getUsername();

        Request request = new Request("New Title", "newDescription", "Test Request", false, account, project);
        request = requestRepository.saveAndFlush(request);

    }

}
