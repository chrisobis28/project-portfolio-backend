package com.team2a.ProjectPortfolio.Controllers;

import com.team2a.ProjectPortfolio.Commons.Media;
import com.team2a.ProjectPortfolio.CustomExceptions.IdIsNullException;
import com.team2a.ProjectPortfolio.CustomExceptions.MediaNotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.ProjectNotFoundException;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
   *
   * @param projectId
   * @return
   */
  @GetMapping("/{projectId}")
  public ResponseEntity<List<Media>> getMediaByProjectId (@PathVariable("projectId") UUID projectId) {
    try {
      List<Media> medias = mediaService.getMediaByProjectId(projectId);
      return ResponseEntity.ok(medias);
    } catch (IdIsNullException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    } catch (ProjectNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   *
   * @param projectId
   * @param path
   * @return
   */
  @PostMapping("/{projectId}")
  public ResponseEntity<Media> addMediaToProject (@PathVariable("projectId") UUID projectId, @RequestBody String path) {
    try {
      return ResponseEntity.ok(mediaService.addMediaToProject(projectId, path));
    }
    catch (IdIsNullException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    catch (ProjectNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   *
   * @param mediaId
   * @return
   */
  @DeleteMapping("/{mediaId}")
  public ResponseEntity<String> deleteMedia (@PathVariable("mediaId") UUID mediaId) {
    try {
      mediaService.deleteMedia(mediaId);
      return ResponseEntity.status(HttpStatus.OK).body("Media deleted successfully.");
    }
    catch (IdIsNullException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    catch (MediaNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }
}
