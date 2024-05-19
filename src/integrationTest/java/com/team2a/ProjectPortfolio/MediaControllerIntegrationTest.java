package com.team2a.ProjectPortfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2a.ProjectPortfolio.Commons.Media;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Repositories.MediaRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MediaControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MediaRepository mediaRepository;

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private UUID projectId;

  private UUID otherProjectId;

  private Project project;

  private Media media;

  @BeforeEach
  public void setUp() {
    mediaRepository.deleteAll();
    projectRepository.deleteAll();

    project = new Project("Project title", "Project Description", "Project Bibtex", false);
    project = projectRepository.saveAndFlush(project);
    projectId = project.getProjectId();

    otherProjectId = UUID.randomUUID();
    while (otherProjectId.equals(projectId)) {
      otherProjectId = UUID.randomUUID();
    }

    media = new Media(project, "Media name 1", "Media path 1");
    Media media2 = new Media(project, "Media name 2", "Media path 2");
    Media media3 = new Media(project, "Media name 3", "Media path 3");

    media = mediaRepository.saveAndFlush(media);
    mediaRepository.saveAndFlush(media2);
    mediaRepository.saveAndFlush(media3);
  }

  @Test
  public void getMediaByProjectId() throws Exception {
    mockMvc.perform(get(Routes.MEDIA + "/" + projectId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0].name", is("Media name 1")))
        .andExpect(jsonPath("$[0].path", is("Media path 1")))
        .andExpect(jsonPath("$[1].name", is("Media name 2")))
        .andExpect(jsonPath("$[1].path", is("Media path 2")))
        .andExpect(jsonPath("$[2].name", is("Media name 3")))
        .andExpect(jsonPath("$[2].path", is("Media path 3")));

    mockMvc.perform(get(Routes.MEDIA + "/" + otherProjectId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    mockMvc.perform(get(Routes.MEDIA + "/" + null)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void addMediaToProject() throws Exception {
    Media addedMedia = new Media(project, "Add Name", "Add Path");
    assertEquals(3, mediaRepository.count());
    mockMvc.perform(post(Routes.MEDIA + "/" + projectId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(addedMedia)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is("Add Name")))
        .andExpect(jsonPath("$.path", is("Add Path")));


    assertEquals(4, mediaRepository.count());
    mockMvc.perform(post(Routes.MEDIA + "/" + otherProjectId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(media)))
        .andExpect(status().isNotFound());

    mockMvc.perform(post(Routes.MEDIA + "/" + projectId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new Media(project, null, "path"))))
        .andExpect(status().isBadRequest());

    mockMvc.perform(post(Routes.MEDIA + "/" + null)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(media)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void deleteMedia() throws Exception {
    assertEquals(3, mediaRepository.count());

    mockMvc.perform(delete(Routes.MEDIA + "/" + media.getMediaId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is("Media deleted successfully.")));

    assertEquals(2, mediaRepository.count());

    mockMvc.perform(delete(Routes.MEDIA + "/" + media.getMediaId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("No media with the id " + media.getMediaId() + " could be found.")));
  }
}