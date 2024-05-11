package com.team2a.ProjectPortfolio.Controllers;

import com.team2a.ProjectPortfolio.Commons.Tag;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.TagService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(Routes.TAGS)
public class TagController {

    private final TagService tagService;

    /**
     * Constructor for the tag controller
     *
     * @param tagService the tag service
     */
    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }


    /**
     * Get all tags by project id
     *
     * @param projectId the project id
     * @return a list of tags
     */
    @GetMapping("/{projectId}")
    public ResponseEntity<List<Tag>> getTagsByProjectId (@PathVariable("projectId") UUID projectId) {
        try {
            List<Tag> tagsList = tagService.getTagsByProjectId(projectId);
            return ResponseEntity.ok(tagsList);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
    }


    /**
     * Create a tag
     *
     * @param tag
     * @return the tag
     */
    @PostMapping("/create")
    public ResponseEntity<Tag> createTag (@RequestBody Tag tag) {
        try {
            Tag newTag = tagService.createTag(tag);
            return ResponseEntity.ok(newTag);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
    }

    /**
     * Add tag to project
     *
     * @param projectId the project id
     * @param tagId     the tag id
     * @return the response entity
     */
    @PostMapping("/{projectId}/{tagId}")
    public ResponseEntity<String> addTagToProject
    (@PathVariable("projectId") UUID projectId, @PathVariable("tagId") UUID tagId) {
        try {
            tagService.addTagToProject(projectId, tagId);
            return ResponseEntity.ok().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    /**
     * Edit a tag
     *
     * @param tag the tag
     * @return the tag
     */
    @PutMapping("/edit")
    public ResponseEntity<Tag> editTag (@RequestBody Tag tag) {
        try {
            Tag newTag = tagService.editTag(tag);
            return ResponseEntity.ok(newTag);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
    }

    /**
     * Delete a tag
     *
     * @param tagId the tag id
     * @return the response entity
     */
    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> deleteTag (@PathVariable("tagId") UUID tagId) {
        tagService.deleteTag(tagId);
        return ResponseEntity.ok().build();
    }

    /**
     * Remove tag from project
     *
     * @param projectId the project id
     * @param tagId     the tag id
     * @return the response entity
     */
    @DeleteMapping("/{projectId}/{tagId}")
    public ResponseEntity<Void> removeTagFromProject
    (@PathVariable("projectId") UUID projectId, @PathVariable("tagId") UUID tagId) {
        tagService.removeTagFromProject(projectId, tagId);
        return ResponseEntity.ok().build();
    }
}
