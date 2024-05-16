package com.team2a.ProjectPortfolio.unit.Services;

import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.Tag;
import com.team2a.ProjectPortfolio.Commons.TagsToProject;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.TagRepository;
import com.team2a.ProjectPortfolio.Repositories.TagToProjectRepository;
import com.team2a.ProjectPortfolio.Services.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {


    private TagRepository tagRepository;

    private TagToProjectRepository tagToProjectRepository;

    private ProjectRepository projectRepository;

    private TagService tagService;

    @BeforeEach
    void setUp() {
        tagRepository = mock(TagRepository.class);
        tagToProjectRepository = mock(TagToProjectRepository.class);
        projectRepository = mock(ProjectRepository.class);
        tagService = new TagService(tagRepository, tagToProjectRepository, projectRepository);
    }

    @Test
    void testCreateTag() {
        Tag tag = new Tag(UUID.randomUUID(), "Test Tag", "Red", null, null);
        when(tagRepository.findByNameAndColor(tag.getName(), tag.getColor())).thenReturn(Optional.empty());
        when(tagRepository.saveAndFlush(tag)).thenReturn(tag);

        Tag createdTag = tagService.createTag(tag);

        assertEquals(tag, createdTag);
    }

    @Test
    void testCreateTagConflict() {
        Tag tag = new Tag(UUID.randomUUID(), "Test Tag", "Red", null, null);
        when(tagRepository.findByNameAndColor(tag.getName(), tag.getColor())).thenReturn(Optional.of(tag));

        assertThrows(ResponseStatusException.class, () -> tagService.createTag(tag));
    }

    @Test
    void testGetTagsByProjectId() {
        UUID projectId = UUID.randomUUID();
        Tag tag = new Tag(UUID.randomUUID(), "Test Tag", "Red", null, null);
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
        Tag tag = new Tag(tagId, "Test Tag", "Red", null, null);
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
        Tag tag = new Tag(UUID.randomUUID(), "Test Tag", "Red", null, null);
        when(tagRepository.findById(tag.getTagId())).thenReturn(Optional.of(tag));
        when(tagRepository.saveAndFlush(tag)).thenReturn(tag);

        Tag editedTag = tagService.editTag(tag);

        assertEquals(tag, editedTag);
    }

    @Test
    void testEditTagNotFound() {
        Tag tag = new Tag(UUID.randomUUID(), "Test Tag", "Red", null, null);
        when(tagRepository.findById(tag.getTagId())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> tagService.editTag(tag));
    }

    @Test
    void testDeleteTag() {
        UUID tagId = UUID.randomUUID();

        assertDoesNotThrow(() -> tagService.deleteTag(tagId));
        verify(tagRepository, times(1)).deleteById(tagId);
    }

    @Test
    void testRemoveTagFromProject() {
        UUID projectId = UUID.randomUUID();
        UUID tagId = UUID.randomUUID();

        assertDoesNotThrow(() -> tagService.removeTagFromProject(projectId, tagId));
        verify(tagToProjectRepository, times(1)).deleteByProjectProjectIdAndTagTagId(projectId, tagId);
    }
}
