package com.team2a.ProjectPortfolio.Controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.team2a.ProjectPortfolio.Commons.Media;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.CustomExceptions.ProjectNotFoundException;
import com.team2a.ProjectPortfolio.Repositories.MediaRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Services.MediaService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
public class MediaControllerTest {

  @Mock
  private MediaRepository mediaRepository;
  @Mock
  private ProjectRepository projectRepository;
  private MediaController mediaController;

  @BeforeEach
  void setup() {
    mediaRepository = Mockito.mock(MediaRepository.class);
    projectRepository = Mockito.mock(ProjectRepository.class);
    mediaController = new MediaController(new MediaService(mediaRepository, projectRepository));
  }

  @Test
  void testGetMediaByProjectIdNullId() {
    ResponseEntity<List<Media>> entity = mediaController.getMediaByProjectId(null);
    assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
  }

  @Test
  void testGetMediaByProjectIdProjectNotFound() {
    UUID x = UUID.randomUUID();
    when(projectRepository.findById(x)).thenReturn(Optional.empty());
    ResponseEntity<List<Media>> entity = mediaController.getMediaByProjectId(x);
    assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
  }

  @Test
  void testGetMediaByProjectIdSuccess() {
    UUID x = UUID.randomUUID();
    Project p = new Project("title", "description", "bibtex", false);
    when(projectRepository.findById(x)).thenReturn(Optional.of(p));
    Media m1 = new Media(p, "path1");
    Media m2 = new Media(p, "path2");
    Media m3 = new Media(p, "path3");
    when(mediaRepository.findAllByProjectProjectId(x)).thenReturn(List.of(m1, m2, m3));
    ResponseEntity<List<Media>> entity = mediaController.getMediaByProjectId(x);
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(3, Objects.requireNonNull(entity.getBody()).size());
    assertEquals(m1, entity.getBody().get(0));
    assertEquals(m2, entity.getBody().get(1));
    assertEquals(m3, entity.getBody().get(2));
  }

  @Test
  void testAddMediaToProjectNullId() {
    ResponseEntity<Media> entity = mediaController.addMediaToProject(null, "path");
    assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
  }

  @Test
  void testAddMediaToProjectProjectNotFound() {
    UUID x = UUID.randomUUID();
    when(projectRepository.findById(x)).thenReturn(Optional.empty());
    ResponseEntity<Media> entity = mediaController.addMediaToProject(x, "path");
    assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
  }

  @Test
  void testAddMediaToProjectSuccess() {
    UUID x = UUID.randomUUID();
    Project p = new Project("title", "description", "bibtex", false);
    when(projectRepository.findById(x)).thenReturn(Optional.of(p));
    ResponseEntity<Media> entity = mediaController.addMediaToProject(x, "path");
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(p, Objects.requireNonNull(entity.getBody()).getProject());
    assertEquals("path", entity.getBody().getPath());
  }

  @Test
  void testDeleteMediaFromProjectNullId() {
    ResponseEntity<String> entity = mediaController.deleteMedia(null);
    assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
    assertEquals("Null id not accepted.", entity.getBody());
  }

  @Test
  void testDeleteMediaFromProjectMediaNotFound() {
    UUID x = UUID.randomUUID();
    when(mediaRepository.findById(x)).thenReturn(Optional.empty());
    ResponseEntity<String> entity = mediaController.deleteMedia(x);
    assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    assertEquals("No media with the id " + x + " could be found.", entity.getBody());
  }

  @Test
  void testDeleteMediaFromProjectSuccess() {
    UUID x = UUID.randomUUID();
    Project p = new Project("title", "description", "bibtex", false);
    when(mediaRepository.findById(x)).thenReturn(Optional.of(new Media(p, "path")));
    ResponseEntity<String> entity = mediaController.deleteMedia(x);
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals("Media deleted successfully.", entity.getBody());
  }
}
