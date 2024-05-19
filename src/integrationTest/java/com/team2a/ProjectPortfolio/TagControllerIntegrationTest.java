package com.team2a.ProjectPortfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.Tag;
import com.team2a.ProjectPortfolio.Commons.TagsToProject;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.TagRepository;
import com.team2a.ProjectPortfolio.Repositories.TagToProjectRepository;
import org.junit.jupiter.api.AfterEach;
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
public class TagControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TagToProjectRepository tagToProjectRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID projectId;

    private Tag tag1;

    private Tag tag2;

    private Tag tag3;

    @BeforeEach
    public void setup() {
        tagToProjectRepository.deleteAll();
        tagRepository.deleteAll();
        projectRepository.deleteAll();

        Project project = new Project("Test Project", "Description", "Bibtex", false);
        project = projectRepository.saveAndFlush(project);
        projectId = project.getProjectId();

        tag1 = new Tag("Tag1", "Red");
        tag2 = new Tag("Tag2", "Blue");
        tag3 = new Tag("Tag3", "Green");

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

    @Test
    public void testCreateTag() throws Exception {
        // Create a tag with a different name and color
        Tag tag4 = new Tag("Tag4", "Yellow");
        mockMvc.perform(post("/tag/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tag4)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("Tag4")))
            .andExpect(jsonPath("$.color", is("Yellow")));

        // Create a tag with a different name but the same color
        Tag tag5 = new Tag("Tag5", "Red");
        mockMvc.perform(post("/tag/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tag5)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("Tag5")))
            .andExpect(jsonPath("$.color", is("Red")));

        // Create a tag with the same name but a different color
        Tag tag6 = new Tag("Tag1", "Yellow");
        mockMvc.perform(post("/tag/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tag6)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("Tag1")))
            .andExpect(jsonPath("$.color", is("Yellow")));

        // Attempt to create a tag with the same name and color, which should fail
        Tag tag7 = new Tag("Tag1", "Red");
        mockMvc.perform(post("/tag/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tag7)))
            .andExpect(status().isConflict());
    }

    @Test
    public void testAddTagToProject() throws Exception {
        Tag tag = new Tag("Tag4", "Yellow");
        tag = tagRepository.saveAndFlush(tag);

        mockMvc.perform(post("/tag/" + projectId + "/" + tag.getTagId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        mockMvc.perform(get("/tag/" + projectId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(4)))
            .andExpect(jsonPath("$[3].name", is("Tag4")));

        mockMvc.perform(post("/tag/" + projectId + "/" + tag.getTagId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());
    }

    @Test
    public void testEditTag() throws Exception {
        Tag tag = new Tag("Tag1", "Yellow");
        tag = tagRepository.saveAndFlush(tag);

        tag.setName("Tag4");
        tag.setColor("Green");

        mockMvc.perform(put("/tag/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tag)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("Tag4")))
            .andExpect(jsonPath("$.color", is("Green")));

        tagRepository.deleteById(tag.getTagId());

        mockMvc.perform(put("/tag/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tag)))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteTag() throws Exception {
        Tag tag4 = new Tag("Tag4", "Yellow");
        tag4 = tagRepository.saveAndFlush(tag4);

        mockMvc.perform(delete("/tag/" + tag4.getTagId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        mockMvc.perform(delete("/tag/" + tag4.getTagId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        mockMvc.perform(delete("/tag/" + tag1.getTagId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        assertEquals(2, tagRepository.findAll().size());

        mockMvc.perform(get("/tag/" + projectId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name", is("Tag2")))
            .andExpect(jsonPath("$[1].name", is("Tag3")));
    }

    @Test
    public void testRemoveTagFromProject() throws Exception{

        mockMvc.perform(delete("/tag/" + projectId + "/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(containsString("Tag or project does not exist")));

        Tag tag = new Tag("Tag4", "Yellow");
        tag = tagRepository.saveAndFlush(tag);

        mockMvc.perform(delete("/tag/" + projectId + "/" + tag.getTagId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(containsString("Tag does not belong to project")));

        mockMvc.perform(delete("/tag/" + projectId + "/" + tag1.getTagId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        mockMvc.perform(get("/tag/" + projectId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name", is("Tag2")))
            .andExpect(jsonPath("$[1].name", is("Tag3")));
    }
}
