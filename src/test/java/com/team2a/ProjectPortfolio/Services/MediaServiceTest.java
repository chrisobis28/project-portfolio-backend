package com.team2a.ProjectPortfolio.Services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.team2a.ProjectPortfolio.Commons.Media;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.Request;
import com.team2a.ProjectPortfolio.Commons.RequestMediaProject;
import com.team2a.ProjectPortfolio.CustomExceptions.FileNotSavedException;
import com.team2a.ProjectPortfolio.CustomExceptions.MediaNotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.NotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.ProjectNotFoundException;
import com.team2a.ProjectPortfolio.Repositories.MediaRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import java.io.IOException;
import java.util.*;

import com.team2a.ProjectPortfolio.Repositories.RequestMediaProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.RequestRepository;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.misc.Triple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
public class MediaServiceTest {

  @Mock
  private MediaRepository mediaRepository;
  @Mock
  private ProjectRepository projectRepository;
  @InjectMocks
  private MediaService mediaService;
  @Mock
  private MediaHelper mediaHelper;

  @Mock
  private RequestRepository requestRepository;

  @Mock
  private RequestMediaProjectRepository requestMediaProject;

  @BeforeEach
  void setUp() {
    mediaHelper = mock(MediaHelper.class);
    mediaRepository = mock(MediaRepository.class);
    projectRepository = mock(ProjectRepository.class);
    requestRepository = mock(RequestRepository.class);
    requestMediaProject = mock(RequestMediaProjectRepository.class);
    mediaService = new MediaService(mediaRepository, projectRepository, requestRepository,
            requestMediaProject);
    mediaService.setMediaHelper(mediaHelper);
  }

  @Test
  void testGetImagesContentByProjectIdNotFound(){
    UUID x = UUID.randomUUID();
    when(projectRepository.findById(x)).thenReturn(Optional.empty());
    assertThrows(ProjectNotFoundException.class, () -> mediaService.getImagesContentByProjectId(x));
  }

  @Test
  void testGetImagesContentByProjectIdSuccess() {
    UUID x = UUID.randomUUID();
    Project p = new Project("title", "description", false);
    p.setProjectId(x);
    when(projectRepository.findById(x)).thenReturn(Optional.of(p));
    Media m1 = new Media("name1", "path1");
    m1.setProject(p);
    Media m2 = new Media("name2", "path2");
    m2.setProject(p);
    Media m3 = new Media("name3", "path3");
    m3.setProject(p);
    when(mediaRepository.findAllByProjectProjectId(x)).thenReturn(List.of(m1, m2, m3));
    String[] returnExample = {"path1","path2","path3"};
    when(mediaHelper.getFiles()).thenReturn(returnExample);
    List<Triple<String, String, String>> expectedList = List.of(
            new Triple<>("path1", "null", "name1"),
            new Triple<>("path2", "null", "name2"),
            new Triple<>("path3", "null", "name3")
    );
    List<Triple<String, String, String>> actualList = mediaService.getImagesContentByProjectId(x).stream().toList();
    assertThat(actualList.toString()).isEqualTo(expectedList.toString());
  }
  @Test
  void getDocumentsByProjectIdNotFound(){
    UUID x = UUID.randomUUID();
    when(projectRepository.findById(x)).thenReturn(Optional.empty());
    assertThrows(ResponseStatusException.class, () -> mediaService.getDocumentsByProjectId(x));
  }
  @Test
  void getDocumentsByProjectIdSuccess(){
    UUID x = UUID.randomUUID();
    Project p = new Project("title", "description", false);
    p.setProjectId(x);
    when(projectRepository.findById(x)).thenReturn(Optional.of(p));
    Media m1 = new Media("name1", "path1");
    m1.setProject(p);
    Media m2 = new Media("name2", "path2");
    m2.setProject(p);
    Media m3 = new Media("name3", "path3");
    m3.setProject(p);
    when(mediaRepository.findAllByProjectProjectId(x)).thenReturn(List.of(m1, m2, m3));
    List<Media> expectedList = List.of(m1,m2,m3);
    List<Media> actualList = mediaService.getDocumentsByProjectId(x).stream().toList();
    assertThat(actualList).isEqualTo(expectedList);
  }
  @Test
  void getDocumentByMediaIdSuccess(){
    UUID x = UUID.randomUUID();
    Project p = new Project("title", "description", false);
    p.setProjectId(x);
    Media m1 = new Media("name1", "path1");
    m1.setProject(p);
    when(mediaRepository.findById(x)).thenReturn(Optional.of(m1));
    when(mediaRepository.findMediaByMediaId(x)).thenReturn(m1);
    when(mediaHelper.getFileContents(m1.getPath())).thenReturn("content1");
    Pair<String, String> expectedList =new Pair<>("path1","content1");
    Pair<String, String> actualPair = mediaService.getDocumentByMediaId(x);
    assertThat(actualPair.toString()).isEqualTo(expectedList.toString());
  }
  @Test
  void getDocumentByMediaIdNotFound(){
    UUID x = UUID.randomUUID();
    when(mediaRepository.findById(x)).thenReturn(Optional.empty());
    assertThrows(ResponseStatusException.class, () -> mediaService.getDocumentByMediaId(x));
  }

  @Test
  void testAddMediaToProjectNotFound() {
    UUID x = UUID.randomUUID();
    when(projectRepository.findById(x)).thenReturn(Optional.empty());
    assertThrows(ResponseStatusException.class, () -> mediaService.addMediaToProject(x, new MockMultipartFile("file", "test.md", "text/plain", "test".getBytes()),"Test"),"test");
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
  void testAddMediaToProjectSuccess(){
    UUID projectId = UUID.randomUUID();
    Project project = new Project();
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    Media media = new Media("name", "path");
    media.setProject(project);
    when(mediaRepository.save(any(Media.class))).thenReturn(media);
    MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "path", "text/plain", "test".getBytes());
    doNothing().when(mediaHelper).saveFile(any(),any(MultipartFile.class));
    Media savedMedia = mediaService.addMediaToProject(projectId, mockMultipartFile, "test");
    assertEquals(project, savedMedia.getProject());
    assertEquals("name", savedMedia.getName());
    assertEquals("path", savedMedia.getPath());
    verify(projectRepository).findById(projectId);
    verify(mediaRepository).save(any(Media.class));
    verify(mediaHelper).saveFile(anyString(),any(MultipartFile.class));
  }
  @Test
  void testAddMediaToProjectError(){
    UUID projectId = UUID.randomUUID();
    Project project = new Project();
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    Media media = new Media("name", "path");
    media.setProject(project);
    MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "path", "text/plain", "test".getBytes());
    doThrow(FileNotSavedException.class).when(mediaHelper).saveFile(any(),any(MultipartFile.class));
    assertThrows(RuntimeException.class, () -> mediaService.addMediaToProject(projectId, mockMultipartFile, "test"));
    verify(projectRepository).findById(projectId);
    verify(mediaHelper).saveFile(anyString(),any(MultipartFile.class));
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


  @Test
  void testGetMediaForRequestOk () {
    Request r = new Request();
    r.setRequestMediaProjects(new ArrayList<>());
    when(requestRepository.findById(any())).thenReturn(Optional.of(r));
    assertEquals(mediaService.getMediaForRequest(any()), new ArrayList<>());
  }

  @Test
  void testGetMediaRequestNotFound () {
    when(requestRepository.findById(any())).thenThrow(new NotFoundException());
    assertThrows(NotFoundException.class, () -> mediaService.getMediaForRequest(UUID.randomUUID()));
  }
  @Test
  void testAddRemovedMediaOk() {
    Request r = new Request();
    when(requestRepository.findById(any())).thenReturn(Optional.of(r));
    Media m = new Media();
    when(mediaRepository.findById(any())).thenReturn(Optional.of(m));
    assertEquals(mediaService.addRemovedMediaToRequest(UUID.randomUUID(),
            UUID.randomUUID()), m);
    verify(requestMediaProject).save(any());
  }

  @Test
  void testAddRemovedMediaNotFound () {
    when(requestRepository.findById(any())).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> mediaService.addRemovedMediaToRequest(UUID.randomUUID(),
            UUID.randomUUID()));
  }

  @Test
  void testAddRemovedMediaNotFound2 () {
    Request r = new Request();
    when(requestRepository.findById(any())).thenReturn(Optional.of(r));
    when(mediaRepository.findById(any())).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> mediaService.addRemovedMediaToRequest(UUID.randomUUID(),
            UUID.randomUUID()));
  }


}
