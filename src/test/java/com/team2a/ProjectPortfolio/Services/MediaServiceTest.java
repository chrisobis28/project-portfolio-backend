package com.team2a.ProjectPortfolio.Services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.team2a.ProjectPortfolio.Commons.Media;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.CustomExceptions.MediaNotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.ProjectNotFoundException;
import com.team2a.ProjectPortfolio.Repositories.MediaRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
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
  void testGetMediaByProjectIdProjectNotFound(){
    UUID x = UUID.randomUUID();
    when(projectRepository.findById(x)).thenReturn(Optional.empty());
    assertThrows(ProjectNotFoundException.class, () -> mediaService.getMediaByProjectId(x));
  }

  @Test
  void testGetMediaByProjectIdProjectSuccess() {
    UUID x = UUID.randomUUID();
    Project p = new Project("title", "description", "bibtex", false);
    p.setProjectId(x);
    when(projectRepository.findById(x)).thenReturn(Optional.of(p));
    Media m1 = new Media(p, "name1", "path1");
    Media m2 = new Media(p, "name2", "path2");
    Media m3 = new Media(p, "name3", "path3");
    when(mediaRepository.findAllByProjectProjectId(x)).thenReturn(List.of(m1, m2, m3));
    System.out.println(mediaService.getMediaByProjectId(x));
    System.out.println(List.of(m1, m2, m3));
    assertEquals(List.of(m1, m2, m3), mediaService.getMediaByProjectId(x));
  }

  @Test
  void testAddMediaToProjectNotFound() {
    UUID x = UUID.randomUUID();
    when(projectRepository.findById(x)).thenReturn(Optional.empty());
    assertThrows(ProjectNotFoundException.class, () -> mediaService.addMediaToProject(x, new Media()));
  }

  @Test
  void testAddMediaToProjectPathNotUnique() {
    UUID x = UUID.randomUUID();
    Project p = new Project();
    when(projectRepository.findById(x)).thenReturn(Optional.of(p));
    when(mediaRepository.findAll()).thenReturn(List.of(new Media(p, "name", "path")));
    assertThrows(IllegalArgumentException.class, () -> mediaService.addMediaToProject(x, new Media(p, "name", "path")));
  }

  @Test
  void testAddMediaToProjectSuccess() {
    UUID x = UUID.randomUUID();
    Project p = new Project();
    when(projectRepository.findById(x)).thenReturn(Optional.of(p));
    Media m = new Media(new Project(), "name", "path");
    when(mediaRepository.save(m)).thenReturn(m);
    Media m2 = mediaService.addMediaToProject(x, m);
    assertEquals(p, m2.getProject());
    assertEquals("name", m2.getName());
    assertEquals("path", m2.getPath());
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
    Media m = new Media(new Project(), "name", "path");
    when(mediaRepository.findById(x)).thenReturn(Optional.of(m));
    doNothing().when(mediaRepository).deleteById(x);
    mediaService.deleteMedia(x);
    verify(mediaRepository, times(1)).deleteById(x);
  }

}
