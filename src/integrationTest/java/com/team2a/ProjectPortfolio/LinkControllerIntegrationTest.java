package com.team2a.ProjectPortfolio;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2a.ProjectPortfolio.Commons.Link;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Repositories.LinkRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.security.SecurityConfigUtils;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters=false)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class LinkControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinkRepository linkRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SecurityConfigUtils securityConfigUtils;
    private UUID projectId;
    private Link link1;
    private Project project;
    @BeforeEach
    public void setup() {
        linkRepository.deleteAll();
        projectRepository.deleteAll();

        project = new Project("Test Project", "Description", false);
        project = projectRepository.saveAndFlush(project);
        projectId = project.getProjectId();

        link1 = new Link("Test1","Test1");
        Link link2 = new Link("Test2","Test2");
        Link link3 = new Link("Test3","Test3");

        link1.setProject(project);
        link2.setProject(project);
        link3.setProject(project);

        link1 = linkRepository.saveAndFlush(link1);
        linkRepository.saveAndFlush(link2);
        linkRepository.saveAndFlush(link3);
        securityConfigUtils.setAuthentication();
    }

    @Test
    public void getLinksByProjectId() throws Exception {
        mockMvc.perform(get(Routes.LINK + "/public/" + projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", is("Test1")))
                .andExpect(jsonPath("$[0].url", is("Test1")))
                .andExpect(jsonPath("$[1].name", is("Test2")))
                .andExpect(jsonPath("$[1].url", is("Test2")))
                .andExpect(jsonPath("$[2].name", is("Test3")))
                .andExpect(jsonPath("$[2].url", is("Test3")));
    }
    @Test
    public void editLinkOfProject() throws Exception {
        link1.setUrl("newURl");
        link1.setName("newName");
        mockMvc.perform(put(Routes.LINK+"/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(link1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("newName")))
                .andExpect(jsonPath("$.url", is("newURl")));

        link1.setLinkId(UUID.randomUUID());
        mockMvc.perform(put(Routes.LINK+"/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(link1)))
                .andExpect(status().isNotFound());
    }
    @Test
    public void addLinkToProject() throws Exception {
       Link link4 = new Link("Test4","Test4");
       link4.setProject(project);
       assertThat(linkRepository.count()).isEqualTo(3);
       mockMvc.perform(post(Routes.LINK+"/"+projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(link4)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test4")))
                .andExpect(jsonPath("$.url", is("Test4")));

       assertThat(linkRepository.count()).isEqualTo(4);
       mockMvc.perform(post(Routes.LINK+"/"+UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(link4)))
                .andExpect(status().isNotFound());
    }
    @Test
    public void addLinkToProjectAlreadyExists() throws Exception {
        Link link4 = new Link("Test4","Test4");
        link4.setProject(project);
        assertThat(linkRepository.count()).isEqualTo(3);
        mockMvc.perform(post(Routes.LINK+"/"+projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(link4)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test4")))
                .andExpect(jsonPath("$.url", is("Test4")));

        assertThat(linkRepository.count()).isEqualTo(4);
        mockMvc.perform(post(Routes.LINK+"/"+projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(link4)))
                .andExpect(status().isConflict());
        assertThat(linkRepository.count()).isEqualTo(4);
        mockMvc.perform(post(Routes.LINK+"/"+UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(link4)))
                .andExpect(status().isNotFound());
    }
    @Test
    public void deleteLinkById() throws Exception{
        assertThat(linkRepository.count()).isEqualTo(3);
        assertThat(linkRepository.findAllByLinkId(link1.getLinkId()).size()).isEqualTo(1);
        mockMvc.perform(delete(Routes.LINK+"/"+link1.getLinkId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("Deleted link")));
        assertThat(linkRepository.count()).isEqualTo(2);
        assertThat(linkRepository.findAllByLinkId(link1.getLinkId()).size()).isEqualTo(0);
        mockMvc.perform(delete(Routes.LINK+"/"+UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
