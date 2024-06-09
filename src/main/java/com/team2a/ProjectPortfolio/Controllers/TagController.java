package com.team2a.ProjectPortfolio.Controllers;

import static com.team2a.ProjectPortfolio.security.Permissions.EDITOR_IN_PROJECT;
import static com.team2a.ProjectPortfolio.security.Permissions.PM_ONLY;

import com.team2a.ProjectPortfolio.Commons.Tag;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.TagService;

import com.team2a.ProjectPortfolio.WebSocket.TagProjectWebSocketHandler;
import com.team2a.ProjectPortfolio.WebSocket.TagWebSocketHandler;
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

    private final TagWebSocketHandler tagWebSocketHandler;

    private final TagProjectWebSocketHandler tagProjectWebSocketHandler;

    /**
     * Constructor for the tag controller
     * @param tagService the tag service
     * @param tagWebSocketHandler the web socket handler for tags
     * @param tagProjectWebSocketHandler the wen socket handler for the tags attributed to a project
     */
    @Autowired
    public TagController(TagService tagService, TagWebSocketHandler tagWebSocketHandler,
                         TagProjectWebSocketHandler tagProjectWebSocketHandler) {
        this.tagService = tagService;
        this.tagWebSocketHandler = tagWebSocketHandler;
        this.tagProjectWebSocketHandler = tagProjectWebSocketHandler;
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
        tagWebSocketHandler.broadcast("tag added");
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
        tagProjectWebSocketHandler.broadcast(projectId.toString());
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
        tagWebSocketHandler.broadcast("tagChanged");
        tagProjectWebSocketHandler.broadcast("all");
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
        tagWebSocketHandler.broadcast("deleted " + tagId);
        tagProjectWebSocketHandler.broadcast("all");
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
        tagProjectWebSocketHandler.broadcast(projectId.toString());
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
