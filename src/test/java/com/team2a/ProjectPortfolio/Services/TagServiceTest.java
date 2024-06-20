package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.Request;
import com.team2a.ProjectPortfolio.Commons.Tag;
import com.team2a.ProjectPortfolio.Commons.TagsToProject;
import com.team2a.ProjectPortfolio.Repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {


    private TagRepository tagRepository;

    private TagToProjectRepository tagToProjectRepository;

    private ProjectRepository projectRepository;

    private TagService tagService;

    @Mock
    private RequestTagProjectRepository requestTagProjectRepository;

    @Mock
    private RequestRepository requestRepository;

    @BeforeEach
    void setUp() {
        tagRepository = mock(TagRepository.class);
        tagToProjectRepository = mock(TagToProjectRepository.class);
        projectRepository = mock(ProjectRepository.class);
        requestTagProjectRepository = mock(RequestTagProjectRepository.class);
        requestRepository = mock(RequestRepository.class);
        tagService = new TagService(tagRepository, tagToProjectRepository, projectRepository,
                requestTagProjectRepository, requestRepository);
    }

    @Test
    void testCreateTag() {
        Tag tag = new Tag("Test Tag", "Red");
        when(tagRepository.findByNameAndColor(tag.getName(), tag.getColor())).thenReturn(Optional.empty());
        when(tagRepository.saveAndFlush(tag)).thenReturn(tag);

        Tag createdTag = tagService.createTag(tag);

        assertEquals(tag, createdTag);
    }

    @Test
    void testCreateTagConflict() {
        Tag tag = new Tag("Test Tag", "Red");
        when(tagRepository.findByNameAndColor(tag.getName(), tag.getColor())).thenReturn(Optional.of(tag));

        assertThrows(ResponseStatusException.class, () -> tagService.createTag(tag));
    }

    @Test
    void testGetTagsByProjectId() {
        UUID projectId = UUID.randomUUID();
        Tag tag = new Tag("Test Tag", "Red");
        TagsToProject tagsToProject = new TagsToProject(tag, new Project());
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(new Project()));
        when(tagToProjectRepository.findAllByProjectProjectId(projectId)).thenReturn(Collections.singletonList(tagsToProject));

        List<Tag> tags = tagService.getTagsByProjectId(projectId);

        assertEquals(1, tags.size());
        assertEquals(tag, tags.get(0));
    }

    @Test
    void testGetTagsByProjectIdProjectNotFound() {
        UUID projectId = UUID.randomUUID();
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> tagService.getTagsByProjectId(projectId));
    }

    @Test
    void testAddTagToProject() {
        UUID projectId = UUID.randomUUID();
        UUID tagId = UUID.randomUUID();
        Tag tag = new Tag("Test Tag", "Red");
        Project project = new Project();
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(tagToProjectRepository.existsByProjectProjectIdAndTagTagId(projectId, tagId)).thenReturn(false);

        assertDoesNotThrow(() -> tagService.addTagToProject(projectId, tagId));
        verify(tagToProjectRepository, times(1)).saveAndFlush(any(TagsToProject.class));
    }

    @Test
    void testAddTagToProjectTagNotFound() {
        UUID projectId = UUID.randomUUID();
        UUID tagId = UUID.randomUUID();
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> tagService.addTagToProject(projectId, tagId));
    }

    @Test
    void testAddTagToProjectTagProjectFound() {
        UUID projectId = UUID.randomUUID();
        UUID tagId = UUID.randomUUID();
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(new Tag()));
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> tagService.addTagToProject(projectId, tagId));
    }

    @Test
    void testAddTagToProjectConflict() {
        UUID projectId = UUID.randomUUID();
        UUID tagId = UUID.randomUUID();
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(new Tag()));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(new Project()));
        when(tagToProjectRepository.existsByProjectProjectIdAndTagTagId(projectId, tagId)).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> tagService.addTagToProject(projectId, tagId));
    }

    @Test
    void testEditTag() {
        Tag tag = new Tag("Test Tag", "Red");
        when(tagRepository.findById(tag.getTagId())).thenReturn(Optional.of(tag));
        when(tagRepository.saveAndFlush(tag)).thenReturn(tag);

        Tag editedTag = tagService.editTag(tag);

        assertEquals(tag, editedTag);
    }

    @Test
    void testEditTagNotFound() {
        Tag tag = new Tag("Test Tag", "Red");
        when(tagRepository.findById(tag.getTagId())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> tagService.editTag(tag));
    }

    @Test
    void testDeleteTag() {
        UUID tagId = UUID.randomUUID();

        when(tagRepository.existsById(tagId)).thenReturn(false);
        assertThrows(ResponseStatusException.class, () -> tagService.deleteTag(tagId));
        Tag tag = new Tag("Test Tag", "Red");
        when(tagRepository.existsById(tag.getTagId())).thenReturn(true);
        tagService.deleteTag(tag.getTagId());
        verify(tagRepository, times(1)).deleteById(tag.getTagId());
    }

    @Test
    void testRemoveTagFromProject() {
        UUID projectId = UUID.randomUUID();
        UUID tagId = UUID.randomUUID();

        when(tagRepository.existsById(tagId)).thenReturn(false);
        when(projectRepository.existsById(projectId)).thenReturn(false);
        assertThrows(ResponseStatusException.class, () -> tagService.removeTagFromProject(projectId, tagId));
        when(tagRepository.existsById(tagId)).thenReturn(true);
        when(projectRepository.existsById(projectId)).thenReturn(false);
        assertThrows(ResponseStatusException.class, () -> tagService.removeTagFromProject(projectId, tagId));
        when(tagRepository.existsById(tagId)).thenReturn(false);
        when(projectRepository.existsById(projectId)).thenReturn(true);
        assertThrows(ResponseStatusException.class, () -> tagService.removeTagFromProject(projectId, tagId));
        when(tagRepository.existsById(tagId)).thenReturn(true);
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(tagToProjectRepository.existsByProjectProjectIdAndTagTagId(projectId, tagId)).thenReturn(false);
        assertThrows(ResponseStatusException.class, () -> tagService.removeTagFromProject(projectId, tagId));
        when(tagToProjectRepository.existsByProjectProjectIdAndTagTagId(projectId, tagId)).thenReturn(true);
        tagService.removeTagFromProject(projectId, tagId);
        verify(tagToProjectRepository, times(1)).deleteByProjectProjectIdAndTagTagId(projectId, tagId);
    }

    @Test
    void testGetAllTags () {
        when(tagRepository.findAll()).thenReturn(List.of(new Tag("tag1", "blue")));
        assertEquals(tagService.getAllTags(), List.of(new Tag("tag1", "blue")));
    }

    @Test
    void testGetTagsForRequestOk () {
        Request r = new Request();
        r.setRequestTagProjects(new ArrayList<>());
        when(requestRepository.findById(any())).thenReturn(Optional.of(r));
        assertEquals(tagService.getTagsForRequest(UUID.randomUUID()), new ArrayList<>());
    }

    @Test
    void testGetTagsForRequestNotFound () {
        when(requestRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, ()-> tagService.getTagsForRequest(UUID.randomUUID()));
    }

    @Test
    void testAddTagToRequestOk () {
        Request r = new Request();
        when(requestRepository.findById(any())).thenReturn(Optional.of(r));
        Tag t = new Tag();
        when(tagRepository.findById(any())).thenReturn(Optional.of(t));
        assertEquals(tagService.addTagToRequest(UUID.randomUUID(), UUID.randomUUID(), false), t);
        verify(requestTagProjectRepository).save(any());

    }

    @Test
    void testAddTagRequestNotFound () {
        when(requestRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> tagService.addTagToRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                false
        ));
    }

    @Test
    void testAddTagRequestNotFound2 () {
        Request r = new Request();
        when(requestRepository.findById(any())).thenReturn(Optional.of(r));
        when(tagRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> tagService.addTagToRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                false
        ));
    }
}
