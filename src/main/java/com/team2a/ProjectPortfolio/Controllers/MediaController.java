package com.team2a.ProjectPortfolio.Controllers;

import com.team2a.ProjectPortfolio.Commons.Media;
import com.team2a.ProjectPortfolio.CustomExceptions.MediaNotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.ProjectNotFoundException;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.MediaService;
import jakarta.validation.Valid;
import org.antlr.v4.runtime.misc.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
     *
     * @param projectId the id of the Project whose Media to be retrieved
     * @return the List of all Medias corresponding to the project
     */
    @GetMapping("/{projectId}")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<List<Triple<String,String,String>>> getMediaByProjectId(@PathVariable("projectId")
                                                                                      UUID projectId) {
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
     * @param file the Media to be added
     * @param name the name of the media
     * @return the Media instance generated and saved
     */
    @PostMapping("/{projectId}")
    public ResponseEntity<Media> addMediaToProject (@PathVariable("projectId") UUID projectId,
                                                    @RequestParam("file") MultipartFile file, @RequestParam String name) {
        try {
            return ResponseEntity.ok(mediaService.addMediaToProject(projectId, file,name));
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
