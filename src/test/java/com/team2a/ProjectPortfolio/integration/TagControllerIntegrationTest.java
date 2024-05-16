package com.team2a.ProjectPortfolio.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.Tag;
import com.team2a.ProjectPortfolio.Commons.TagsToProject;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.TagRepository;
import com.team2a.ProjectPortfolio.Repositories.TagToProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
public class TagControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TagToProjectRepository tagToProjectRepository;

    private UUID projectId;

    @BeforeEach
    public void setup() {
        tagToProjectRepository.deleteAll();
        tagRepository.deleteAll();
        projectRepository.deleteAll();

        Project project = new Project("Test Project", "Description", "Bibtex", false);
        project = projectRepository.saveAndFlush(project);
        projectId = project.getProjectId();

        Tag tag1 = new Tag(null, "Tag1", "Red", null, null);
        Tag tag2 = new Tag(null, "Tag2", "Blue", null, null);
        Tag tag3 = new Tag(null, "Tag3", "Green", null, null);

        tag1 = tagRepository.saveAndFlush(tag1);
        tag2 = tagRepository.saveAndFlush(tag2);
        tag3 = tagRepository.saveAndFlush(tag3);

        tagToProjectRepository.saveAndFlush(new TagsToProject(tag1, project));
        tagToProjectRepository.saveAndFlush(new TagsToProject(tag2, project));
        tagToProjectRepository.saveAndFlush(new TagsToProject(tag3, project));

    }

    @Test
    public void testGetTagsByProjectId() throws Exception {
        mockMvc.perform(get("/tag/" + projectId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].name", is("Tag1")))
            .andExpect(jsonPath("$[1].name", is("Tag2")))
            .andExpect(jsonPath("$[2].name", is("Tag3")));
    }
}
