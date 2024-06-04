package com.team2a.ProjectPortfolio.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2a.ProjectPortfolio.Commons.Project;
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
public class ProjectAuthorizationIntegrationTest {

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
    public void testCreateProjectUnauthorized() throws Exception {
        securityConfigUtils.setUserWithUsername("username");
        mockMvc.perform(post(Routes.PROJECT + "/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(project1)))
            .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateProjectAuthorized() throws Exception {
        securityConfigUtils.setProjectManagerWithUsername("username1");
        accountRepository.save(securityConfigUtils.getAccount());
        mockMvc.perform(post(Routes.PROJECT + "/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(project1)))
            .andExpect(status().isOk());
    }

    @Test
    public void testUpdateProjectUnauthorizedDoesNotBelongToProject() throws Exception {
        securityConfigUtils.setProjectManagerWithUsername("username1");
        accountRepository.save(securityConfigUtils.getAccount());
        mockMvc.perform(post(Routes.PROJECT + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(project1)))
            .andExpect(status().isOk());
        securityConfigUtils.setUserWithUsername("username2");
        accountRepository.save(securityConfigUtils.getAccount());

        UUID project1Id = projectRepository.findAll().get(0).getProjectId();
        Project project2 = new Project("title2", "description2", false);
        mockMvc.perform(put(Routes.PROJECT + "/" + project1Id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(project2)))
            .andExpect(status().isForbidden());
    }

    @Test
    public void testUpdateProjectAuthorizedProjectManager() throws Exception {
        securityConfigUtils.setProjectManagerWithUsername("username1");
        accountRepository.save(securityConfigUtils.getAccount());
        mockMvc.perform(post(Routes.PROJECT + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(project1)))
            .andExpect(status().isOk());

        UUID project1Id = projectRepository.findAll().get(0).getProjectId();
        Project project2 = new Project("title2", "description2", false);
        mockMvc.perform(put(Routes.PROJECT + "/" + project1Id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(project2)))
            .andExpect(status().isOk());
    }
}
