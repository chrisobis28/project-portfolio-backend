package com.team2a.ProjectPortfolio.Services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
    Media m1 = new Media("name1", "path1");
    Media m2 = new Media("name2", "path2");
    Media m3 = new Media("name3", "path3");
    when(mediaRepository.findAllByProjectProjectId(x)).thenReturn(List.of(m1, m2, m3));
    System.out.println(mediaService.getMediaByProjectId(x));
    System.out.println(List.of(m1, m2, m3));
    assertEquals(List.of(), mediaService.getMediaByProjectId(x));
  }

  @Test
  void testAddMediaToProjectNotFound() {
    UUID x = UUID.randomUUID();
    when(projectRepository.findById(x)).thenReturn(Optional.empty());
    assertThrows(ProjectNotFoundException.class, () -> mediaService.addMediaToProject(x, new MockMultipartFile("file", "test.md", "text/plain", "test".getBytes()),"Test"),"test");
  }

  @Test
  void testAddMediaToProjectPathNotUnique() {
    UUID x = UUID.randomUUID();
    Project p = new Project();
    when(projectRepository.findById(x)).thenReturn(Optional.of(p));
    when(mediaRepository.findAll()).thenReturn(List.of(new Media("name", "path")));
    assertThrows(ResponseStatusException.class, () -> mediaService.addMediaToProject(x, new MockMultipartFile("name", "path", "text/plain", "test".getBytes()),"Test"),"test");
  }

  @Test
  void testAddMediaToProjectSuccess() {
    UUID x = UUID.randomUUID();
    Project p = new Project();
    when(projectRepository.findById(x)).thenReturn(Optional.of(p));
    Media m = new Media("name", "path");
    m.setProject(p);
    when(mediaRepository.save(any(Media.class))).thenReturn(m);
    MockMultipartFile mp = new MockMultipartFile("name", "path", "text/plain", "test".getBytes());
    Media m2 = mediaService.addMediaToProject(x, mp,"test");
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
    Media m = new Media("name", "path");
    when(mediaRepository.findById(x)).thenReturn(Optional.of(m));
    doNothing().when(mediaRepository).deleteById(x);
    mediaService.deleteMedia(x);
    verify(mediaRepository, times(1)).deleteById(x);
  }

  @Test
  void testEditMediaNotFound() {
    UUID id = UUID.randomUUID();
    Media media = new Media();
    media.setMediaId(id);
    when(mediaRepository.findById(id)).thenReturn(Optional.empty());
    assertThrows(ResponseStatusException.class, () -> mediaService.editMedia(media));
  }

  @Test
  void testEditMediaPathNotUnique() {
    UUID id = UUID.randomUUID();
    Media media = new Media();
    media.setMediaId(id);
    media.setPath("path");
    when(mediaRepository.findAll()).thenReturn(List.of(new Media("name", "path")));
    when(mediaRepository.findById(id)).thenReturn(Optional.of(new Media("name", "some_other_path")));
    assertThrows(ResponseStatusException.class, () -> mediaService.editMedia(media));
  }

  @Test
  void testEditMediaSuccess() {
    UUID id = UUID.randomUUID();
    Media media = new Media();
    media.setMediaId(id);
    media.setPath("path");
    when(mediaRepository.findAll()).thenReturn(List.of());
    when(mediaRepository.findById(id)).thenReturn(Optional.of(new Media()));
    when(mediaRepository.save(media)).thenReturn(media);
    assertEquals(media, mediaService.editMedia(media));
  }

}
