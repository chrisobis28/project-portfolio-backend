package com.team2a.ProjectPortfolio.Controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.team2a.ProjectPortfolio.Commons.Media;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.CustomExceptions.MediaNotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.ProjectNotFoundException;
import com.team2a.ProjectPortfolio.Services.MediaService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
public class MediaControllerTest {

  @Mock
  private MediaService mediaService;
  private MediaController mediaController;

  @BeforeEach
  void setup() {
    mediaService = Mockito.mock(MediaService.class);
    mediaController = new MediaController(mediaService);
  }

  @Test
  void testGetMediaByProjectIdProjectNotFoundException() {
    when(mediaService.getMediaByProjectId(any(UUID.class))).thenThrow(new ProjectNotFoundException(""));
    ResponseEntity<List<Media>> entity = mediaController.getMediaByProjectId(UUID.randomUUID());
    assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    assertNull(entity.getBody());
  }

  @Test
  void testGetMediaByProjectIdSuccess() {
    Project p = new Project("title", "description", "bibtex", false);
    Media m1 = new Media(p, "name", "path1");
    Media m2 = new Media(p, "name", "path2");
    Media m3 = new Media(p, "name", "path3");
    when(mediaService.getMediaByProjectId(any(UUID.class))).thenReturn(List.of(m1, m2, m3));
    ResponseEntity<List<Media>> entity = mediaController.getMediaByProjectId(UUID.randomUUID());
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(List.of(m1, m2, m3), entity.getBody());
  }

  @Test
  void testAddMediaToProjectProjectNotFoundException() {
    when(mediaService.addMediaToProject(any(UUID.class), any(Media.class))).thenThrow(new ProjectNotFoundException(""));
    ResponseEntity<Media> entity = mediaController.addMediaToProject(UUID.randomUUID(), new Media());
    assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    assertNull(entity.getBody());
  }

  @Test
  void testAddMediaToProjectPathNotUnique() {
    when(mediaService.addMediaToProject(any(UUID.class), any(Media.class))).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
    assertThrows(ResponseStatusException.class, () -> mediaController.addMediaToProject(UUID.randomUUID(), new Media()));
  }

  @Test
  void testAddMediaToProjectSuccess() {
    Project p = new Project("title", "description", "bibtex", false);
    Media m1 = new Media(p, "name", "path1");
    when(mediaService.addMediaToProject(any(UUID.class), any(Media.class))).thenReturn(m1);
    ResponseEntity<Media> entity = mediaController.addMediaToProject(UUID.randomUUID(), new Media());
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(m1, entity.getBody());
  }

  @Test
  void testDeleteMediaFromProjectMediaNotFound() {
    UUID i1 = UUID.randomUUID();
    doThrow(new MediaNotFoundException("No media with the id " + i1 + " could be found."))
        .when(mediaService).deleteMedia(i1);
    ResponseEntity<String> entity = mediaController.deleteMedia(i1);
    assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    assertEquals("No media with the id " + i1 + " could be found.", entity.getBody());
  }

  @Test
  void testDeleteMediaFromProjectSuccess() {
    doNothing().when(mediaService).deleteMedia(any());
    ResponseEntity<String> entity = mediaController.deleteMedia(UUID.randomUUID());
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals("Media deleted successfully.", entity.getBody());
  }

  @Test
  void testEditMediaNotFound() {
    Media media = new Media();
    when(mediaService.editMedia(media)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
    assertThrows(ResponseStatusException.class, () -> mediaController.editMedia(media));
  }

  @Test
  void testEditMediaForbiddenPath() {
    Media media = new Media();
    when(mediaService.editMedia(media)).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
    assertThrows(ResponseStatusException.class, () -> mediaController.editMedia(media));
  }

  @Test
  void testMediaSuccess() {
    Media media = new Media();
    when(mediaService.editMedia(media)).thenReturn(media);
    ResponseEntity<Media> entity = mediaController.editMedia(media);
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(media, mediaService.editMedia(media));
  }
}
