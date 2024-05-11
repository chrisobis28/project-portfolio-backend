package com.team2a.ProjectPortfolio.Controllers;

import com.team2a.ProjectPortfolio.Commons.Tag;
import com.team2a.ProjectPortfolio.Services.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagControllerTest {

    @Mock
    private TagService tagService;

    @InjectMocks
    private TagController tagController;

    private Tag tag;
    private UUID projectId;
    private UUID tagId;

    @BeforeEach
    void setUp() {
        tag = new Tag(UUID.randomUUID(), "Test Tag", "Red", null, null);
        projectId = UUID.randomUUID();
        tagId = UUID.randomUUID();
    }

    @Test
    void testGetTagsByProjectId() {
        List<Tag> tagsList = new ArrayList<>();
        tagsList.add(tag);

        when(tagService.getTagsByProjectId(projectId)).thenReturn(tagsList);

        ResponseEntity<List<Tag>> response = tagController.getTagsByProjectId(projectId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tagsList, response.getBody());
    }

    @Test
    void testGetTagsByProjectIdNotFound() {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
            .when(tagService).getTagsByProjectId(projectId);

        ResponseEntity<List<Tag>> response = tagController.getTagsByProjectId(projectId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testCreateTag() {
        when(tagService.createTag(any(Tag.class))).thenReturn(tag);

        ResponseEntity<Tag> response = tagController.createTag(tag);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tag, response.getBody());
    }

    @Test
    void testCreateTagConflict() {
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT))
            .when(tagService).createTag(any(Tag.class));
        ResponseEntity<Tag> response = tagController.createTag(tag);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testAddTagToProject() {
        doNothing().when(tagService).addTagToProject(projectId, tagId);

        ResponseEntity<String> response = tagController.addTagToProject(projectId, tagId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testAddTagToProjectConflict() {
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Tag already exists in project"))
            .when(tagService).addTagToProject(projectId, tagId);

        ResponseEntity<String> response = tagController.addTagToProject(projectId, tagId);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Tag already exists in project", response.getBody());
    }

    @Test
    void testEditTag() {
        when(tagService.editTag(any(Tag.class))).thenReturn(tag);

        ResponseEntity<Tag> response = tagController.editTag(tag);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tag, response.getBody());
    }

    @Test
    void testEditTagNotFound() {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
            .when(tagService).editTag(any(Tag.class));
        ResponseEntity<Tag> response = tagController.editTag(tag);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteTag() {
        doNothing().when(tagService).deleteTag(tagId);

        ResponseEntity<Void> response = tagController.deleteTag(tagId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testRemoveTagFromProject() {
        doNothing().when(tagService).removeTagFromProject(projectId, tagId);

        ResponseEntity<Void> response = tagController.removeTagFromProject(projectId, tagId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
