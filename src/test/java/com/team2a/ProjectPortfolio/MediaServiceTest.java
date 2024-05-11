package com.team2a.ProjectPortfolio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.team2a.ProjectPortfolio.Commons.Media;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.CustomExceptions.IdIsNullException;
import com.team2a.ProjectPortfolio.CustomExceptions.MediaNotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.ProjectNotFoundException;
import com.team2a.ProjectPortfolio.Repositories.MediaRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Services.MediaService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
public class MediaServiceTest {

  @Mock
  private MediaRepository mediaRepository;
  @Mock
  private ProjectRepository projectRepository;

  private MediaService mediaService;

  @BeforeEach
  void setUp() {
    mediaRepository = Mockito.mock(MediaRepository.class);
    projectRepository = Mockito.mock(ProjectRepository.class);
    mediaService = new MediaService(mediaRepository, projectRepository);
  }

  @Test
  void testGetMediaByProjectIdNull(){
    assertThrows(IdIsNullException.class, () -> mediaService.getMediaByProjectId(null));
  }

  @Test
  void testGetMediaByProjectIdProjectNotFound(){
    UUID x = UUID.randomUUID();
    when(projectRepository.findById(x)).thenReturn(Optional.empty());
    assertThrows(ProjectNotFoundException.class, () -> mediaService.getMediaByProjectId(x));
  }

  @Test
  void testGetMediaByProjectIdProjectSuccess() {
    UUID x = UUID.randomUUID();
    Project p = new Project("title", "description", "bibtex", false);
    when(projectRepository.findById(x)).thenReturn(Optional.of(p));
    Media m1 = new Media(p, "path1");
    Media m2 = new Media(p, "path2");
    Media m3 = new Media(p, "path3");
    when(mediaRepository.findAllByProjectProjectId(x)).thenReturn(List.of(m1, m2, m3));
    assertEquals(List.of(m1, m2, m3), mediaService.getMediaByProjectId(x));
  }

  @Test
  void testAddMediaToProjectIdNull() {
    assertThrows(IdIsNullException.class, () -> mediaService.addMediaToProject(null, "path"));
  }

  @Test
  void testAddMediaToProjectNotFound() {
    UUID x = UUID.randomUUID();
    when(projectRepository.findById(x)).thenReturn(Optional.empty());
    assertThrows(ProjectNotFoundException.class, () -> mediaService.addMediaToProject(x, "path"));
  }

  @Test
  void testAddMediaToProjectSuccess() {
    UUID x = UUID.randomUUID();
    Project p = new Project();
    when(projectRepository.findById(x)).thenReturn(Optional.of(p));
    Media m2 = mediaService.addMediaToProject(x, "path");
    assertEquals(m2.getProject(), p);
    assertEquals(m2.getPath(), "path");
  }

  @Test
  void testDeleteMediaIdNull(){
    assertThrows(IdIsNullException.class, () -> mediaService.deleteMedia(null));
  }

  @Test
  void testDeleteMediaMediaNotFound(){
    UUID x = UUID.randomUUID();
    when(mediaRepository.findById(x)).thenReturn(Optional.empty());
    assertThrows(MediaNotFoundException.class, () -> mediaService.deleteMedia(x));
  }

  @Test
  void testDeleteMediaFromProjectSuccess(){
    UUID x = UUID.randomUUID();
    Media m = new Media(new Project(), "path");
    when(mediaRepository.findById(x)).thenReturn(Optional.of(m));
    doNothing().when(mediaRepository).deleteById(x);
    mediaService.deleteMedia(x);
  }

}
