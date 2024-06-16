package com.team2a.ProjectPortfolio;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2a.ProjectPortfolio.Commons.Collaborator;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.ProjectsToAccounts;
import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import com.team2a.ProjectPortfolio.Repositories.CollaboratorRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectsToAccountsRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectsToCollaboratorsRepository;
import com.team2a.ProjectPortfolio.security.SecurityConfigUtils;
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
public class ProjectControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SecurityConfigUtils securityConfigUtils;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CollaboratorRepository collaboratorRepository;

    @Autowired
    private ProjectsToCollaboratorsRepository projectsToCollaboratorsRepository;

    @Autowired
    private ProjectsToAccountsRepository projectsToAccountsRepository;
    @Autowired
    private ObjectMapper objectMapper;
    private Project project1;
    private Project project2;
    private Project project3;

    @BeforeEach
    public void setUp() {
        projectRepository.deleteAll();
        collaboratorRepository.deleteAll();
        project1 = new Project("title1", "description1", false);
        project2 = new Project("title2", "description2", true);
        project3 = new Project("title3", "description3", false);
        project1 = projectRepository.saveAndFlush(project1);
        project2 = projectRepository.saveAndFlush(project2);
        project3 = projectRepository.saveAndFlush(project3);
        securityConfigUtils.setAuthentication();
        accountRepository.saveAndFlush(securityConfigUtils.getAccount());
        collaboratorRepository.saveAndFlush(new Collaborator(securityConfigUtils.getAccount().getName()));
    }

    @Test
    public void getProjects() throws Exception {
        assertEquals(3, projectRepository.count());

        mockMvc.perform(get(Routes.PROJECT + "/public/")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].title", is("title1")))
                .andExpect(jsonPath("$[1].title", is("title2")))
                .andExpect(jsonPath("$[2].title", is("title3")))
                .andExpect(jsonPath("$[0].description", is("description1")))
                .andExpect(jsonPath("$[1].description", is("description2")))
                .andExpect(jsonPath("$[2].description", is("description3")))
                .andExpect(jsonPath("$[0].archived", is(false)))
                .andExpect(jsonPath("$[1].archived", is(true)))
                .andExpect(jsonPath("$[2].archived", is(false)));

        projectRepository.deleteAll();
        assertEquals(0, projectRepository.count());

        mockMvc.perform(get(Routes.PROJECT + "/public/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void deleteProject() throws Exception {
        assertEquals(3, projectRepository.count());

        Project project4 = new Project("title4", "description4", false);
        project4 = projectRepository.saveAndFlush(project4);
        assertEquals(4, projectRepository.count());

        mockMvc.perform(delete(Routes.PROJECT + "/" + project4.getProjectId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertEquals(3, projectRepository.count());

        mockMvc.perform(delete(Routes.PROJECT + "/" + project2.getProjectId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(Routes.PROJECT + "/public/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("title1")))
                .andExpect(jsonPath("$[1].title", is("title3")))
                .andExpect(jsonPath("$[0].description", is("description1")))
                .andExpect(jsonPath("$[1].description", is("description3")))
                .andExpect(jsonPath("$[0].archived", is(false)))
                .andExpect(jsonPath("$[1].archived", is(false)));
    }

    @Test
    public void updateProject() throws Exception {
        assertEquals(3, projectRepository.count());

        Project project4 = new Project("title4", "description4", false);
        project4 = projectRepository.saveAndFlush(project4);
        assertEquals(4, projectRepository.count());
        Project project5 = new Project("title5", "description5", true);

        mockMvc.perform(put(Routes.PROJECT + "/" + project4.getProjectId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(project5)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("title5")))
                .andExpect(jsonPath("$.description", is("description5")))
                .andExpect(jsonPath("$.archived", is(true)));

        projectRepository.deleteById(project4.getProjectId());

        mockMvc.perform(put(Routes.PROJECT + "/" + project4.getProjectId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project5)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getProjectById() throws Exception {
        assertEquals(3, projectRepository.count());

        mockMvc.perform(get(Routes.PROJECT + "/public/" + project3.getProjectId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("title3")))
                .andExpect(jsonPath("$.description", is("description3")))
                .andExpect(jsonPath("$.archived", is(false)));

        Project project4 = new Project("title4", "description4", false);
        project4 = projectRepository.saveAndFlush(project4);
        UUID projectId = project4.getProjectId();

        mockMvc.perform(get(Routes.PROJECT + "/public/" + projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("title4")))
                .andExpect(jsonPath("$.description", is("description4")))
                .andExpect(jsonPath("$.archived", is(false)));

        projectRepository.deleteById(projectId);

        mockMvc.perform(get(Routes.PROJECT + "/public/" + projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createProject() throws Exception {
        assertEquals(3, projectRepository.count());

        Project project4 = new Project("title4", "description4", false);

        mockMvc.perform(post(Routes.PROJECT + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project4)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("title4")))
                .andExpect(jsonPath("$.description", is("description4")))
                .andExpect(jsonPath("$.archived", is(false)));

        assertEquals(4, projectRepository.count());

        Project createdProject = projectRepository.findFirstByTitleAndDescription("title4", "description4").orElseThrow(() ->
            new AssertionError("Project not found in the repository"));

        assertEquals(1, projectsToAccountsRepository.count());
        ProjectsToAccounts pta = projectsToAccountsRepository.findAll().get(0);
        assertEquals(createdProject.getProjectId(), pta.getProject().getProjectId());
        assertEquals(securityConfigUtils.getAccount().getUsername(), pta.getAccount().getUsername());
        assertEquals("PM", pta.getRole().toString());
    }

}
