package com.team2a.ProjectPortfolio.Controllers;

import static com.team2a.ProjectPortfolio.security.Permissions.EDITOR_IN_PROJECT;
import static com.team2a.ProjectPortfolio.security.Permissions.PM_ONLY;

import com.team2a.ProjectPortfolio.Commons.Tag;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.TagService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Routes.TAGS)
@CrossOrigin("http://localhost:4200")
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
    @GetMapping("/public/{projectId}")
    public ResponseEntity<List<Tag>> getTagsByProjectId (@PathVariable("projectId") UUID projectId) {
        List<Tag> tagsList = tagService.getTagsByProjectId(projectId);
        return ResponseEntity.ok(tagsList);
    }


    /**
     * Create a tag
     *
     * @param tag the tag to be created
     * @return the tag
     */
    @PostMapping("/create")
    @PreAuthorize(PM_ONLY)
    public ResponseEntity<Tag> createTag (@Valid @RequestBody Tag tag) {
        Tag newTag = tagService.createTag(tag);
        return ResponseEntity.ok(newTag);
    }

    /**
     * Add tag to project
     *
     * @param projectId the project id
     * @param tagId     the tag id
     * @return the response entity
     */
    @PostMapping("/{projectId}/{tagId}")
    @PreAuthorize(EDITOR_IN_PROJECT)
    public ResponseEntity<String> addTagToProject
    (@PathVariable("projectId") UUID projectId, @PathVariable("tagId") UUID tagId) {
        tagService.addTagToProject(projectId, tagId);
        return ResponseEntity.ok().build();
    }

    /**
     * Edit a tag
     *
     * @param tag the tag
     * @return the tag
     */
    @PutMapping("/edit")
    @PreAuthorize(PM_ONLY)
    public ResponseEntity<Tag> editTag (@Valid @RequestBody Tag tag) {
        Tag newTag = tagService.editTag(tag);
        return ResponseEntity.ok(newTag);
    }

    /**
     * Delete a tag
     *
     * @param tagId the tag id
     * @return the response entity
     */
    @DeleteMapping("/{tagId}")
    @PreAuthorize(PM_ONLY)
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
    @PreAuthorize(EDITOR_IN_PROJECT)
    public ResponseEntity<Void> removeTagFromProject
    (@PathVariable("projectId") UUID projectId, @PathVariable("tagId") UUID tagId) {
        tagService.removeTagFromProject(projectId, tagId);
        return ResponseEntity.ok().build();
    }


    /**
     * Get all tags from the database.
     * @return a list of all tags
     */
    @GetMapping("/public/")
    public ResponseEntity<List<Tag>> getAllTags () {

        List<Tag> tags = tagService.getAllTags();
        return new ResponseEntity<>(tags, HttpStatus.OK);

    }
}
