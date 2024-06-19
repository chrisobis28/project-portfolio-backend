package com.team2a.ProjectPortfolio.Controllers;

import static com.team2a.ProjectPortfolio.Routes.hostLink;
import static com.team2a.ProjectPortfolio.security.Permissions.EDITOR_IN_PROJECT;

import com.team2a.ProjectPortfolio.Commons.Media;
import com.team2a.ProjectPortfolio.CustomExceptions.MediaNotFoundException;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.MediaService;
import com.team2a.ProjectPortfolio.WebSocket.MediaProjectWebSocketHandler;
import com.team2a.ProjectPortfolio.dto.MediaFileContent;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Routes.MEDIA)
@CrossOrigin(hostLink)
public class MediaController {

    private final MediaService mediaService;

    private final MediaProjectWebSocketHandler mediaProjectWebSocketHandler;

    /**
     * Constructor for the media controller
     * @param mediaService the media service instance
     * @param mediaProjectWebSocketHandler the web socket handler for media to project
     */
    @Autowired
    public MediaController (MediaService mediaService,
                            MediaProjectWebSocketHandler mediaProjectWebSocketHandler) {
        this.mediaService = mediaService;
        this.mediaProjectWebSocketHandler = mediaProjectWebSocketHandler;
    }

    /**
     * Gets all Medias under a certain Project
     * @param projectId the id of the Project whose Media to be retrieved
     * @return the List of all Medias corresponding to the project
     */
    @GetMapping("/public/images/{projectId}")
    public ResponseEntity<List<MediaFileContent>> getImagesContentByProjectId (@PathVariable("projectId")
                                                                                               UUID projectId) {
        return ResponseEntity.ok(mediaService.getImagesContentByProjectId(projectId));
    }

    /**
     * Returns the content of a document based on its mediaId
     * @param mediaId the mediaId of the document we need to retrieve
     * @return the media content
     */
    @GetMapping("/public/file/content/{mediaId}")
    public ResponseEntity<MediaFileContent> getDocumentContentByMediaId (@PathVariable("mediaId") UUID mediaId) {
        return ResponseEntity.ok(mediaService.getDocumentByMediaId(mediaId));
    }

    /**
     * Returns the list of medias of a specific projectId
     * @param projectId the projectID
     * @return the list of medias
     */
    @GetMapping("/public/file/{projectId}")
    public ResponseEntity<List<Media>> getDocumentsByProjectId (@PathVariable("projectId") UUID projectId) {
        return ResponseEntity.ok(mediaService.getDocumentsByProjectId(projectId));
    }

    /**
     * Adds a Media associated with an already existing project
     * @param projectId the id of the Project that gets the Media
     * @param file the Media to be added
     * @param name the name of the media
     * @return the Media instance generated and saved
     */
    @PostMapping("/{projectId}")
    @PreAuthorize(EDITOR_IN_PROJECT)
    public ResponseEntity<Media> addMediaToProject (@PathVariable("projectId") UUID projectId,
                                                    @RequestParam("file") MultipartFile file, @RequestParam String name) {
        Media body = mediaService.addMediaToProject(projectId, file,name);
        mediaProjectWebSocketHandler.broadcast(projectId.toString());
        return ResponseEntity.ok(body);
    }

    /**
     * Deletes a media from the database
     * @param mediaId the id of the Media under deletion
     * @param projectId the id of the Project that the Media belongs to
     * @return the status of the operation
     */
    @DeleteMapping("/{projectId}/{mediaId}")
    @PreAuthorize(EDITOR_IN_PROJECT)
    public ResponseEntity<String> deleteMedia (@PathVariable("projectId") UUID projectId,
                                               @PathVariable("mediaId") UUID mediaId) {
        try {
            Media m = mediaService.deleteMedia(mediaId);
            mediaProjectWebSocketHandler.broadcast(m.getProject().getProjectId().toString());
            return ResponseEntity.status(HttpStatus.OK).body("Media deleted successfully.");
        }
        catch (MediaNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Edit Media provided it exists already by id
     * @param media - the Media with the new fields
     * @return - the edited Media
     */
    @PutMapping("/")
    public ResponseEntity<Media> editMedia (@Valid @RequestBody Media media) {
        Media body = mediaService.editMedia(media);
        mediaProjectWebSocketHandler.broadcast(media.getProject().getProjectId().toString());
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
}
