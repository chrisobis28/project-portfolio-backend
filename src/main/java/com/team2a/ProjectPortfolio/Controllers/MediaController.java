package com.team2a.ProjectPortfolio.Controllers;

import com.team2a.ProjectPortfolio.Commons.Media;
import com.team2a.ProjectPortfolio.CustomExceptions.MediaNotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.ProjectNotFoundException;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.MediaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Routes.MEDIA)
public class MediaController {

    private final MediaService mediaService;

    /**
     * Constructor for the Media Controller
     * @param mediaService - the Media Service
     */
    @Autowired
    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    /**
     * Gets all Medias under a certain Project
     * @param projectId the id of the Project whose Media to be retrieved
     * @return the List of all Medias corresponding to the project
     */
    @GetMapping("/{projectId}")
    public ResponseEntity<List<Media>> getMediaByProjectId (@PathVariable("projectId") UUID projectId) {
        try {
            return ResponseEntity.ok(mediaService.getMediaByProjectId(projectId));
        }
        catch (ProjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Adds a Media associated with an already existing project
     * @param projectId the id of the Project that gets the Media
     * @param media the Media to be added
     * @return the Media instance generated and saved
     */
    @PostMapping("/{projectId}")
    public ResponseEntity<Media> addMediaToProject (@PathVariable("projectId") UUID projectId,
                                                    @Valid @RequestBody Media media) {
        try {
            return ResponseEntity.ok(mediaService.addMediaToProject(projectId, media));
        }
        catch (ProjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Deletes a media from the database
     * @param mediaId the id of the Media under deletion
     * @return the status of the operation
     */
    @DeleteMapping("/{mediaId}")
    public ResponseEntity<String> deleteMedia (@PathVariable("mediaId") UUID mediaId) {
        try {
            mediaService.deleteMedia(mediaId);
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
    @PutMapping("")
    public ResponseEntity<Media> editMedia (@Valid @RequestBody Media media) {
        return ResponseEntity.status(HttpStatus.OK).body(mediaService.editMedia(media));
    }
}
