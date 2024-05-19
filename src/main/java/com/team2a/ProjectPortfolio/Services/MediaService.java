package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Media;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.CustomExceptions.MediaNotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.ProjectNotFoundException;
import com.team2a.ProjectPortfolio.Repositories.MediaRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MediaService {

    private final MediaRepository mediaRepository;
    private final ProjectRepository projectRepository;

    /**
     * Constructor for the Media Service
     * @param mediaRepository the Media Repository
     * @param projectRepository the Project Repository
     */
    @Autowired
    public MediaService(MediaRepository mediaRepository, ProjectRepository projectRepository) {
        this.mediaRepository = mediaRepository;
        this.projectRepository = projectRepository;
    }

    /**
     * Returns all the Medias corresponding to a specific Project
     * @param projectId the id of the Project for which we retrieve the Medias
     * @return the list of retrieved Medias
     * @throws RuntimeException - Project doesn't exist or the id is null
     */
    public List<Media> getMediaByProjectId (UUID projectId) throws RuntimeException {
        checkProjectExistence(projectId);
        return mediaRepository.findAllByProjectProjectId(projectId);
    }

    /**
     * Adds a Media to a specific Project
     * @param projectId the id of the Project that gets a new media
     * @param media the Media to be added
     * @return the Media that was added
     * @throws RuntimeException - Project doesn't exist or the id is null
     */
    public Media addMediaToProject (UUID projectId, Media media) throws RuntimeException {
        Project p = checkProjectExistence(projectId);
        checkPathUniqueness(media.getPath());
        media.setProject(p);
        return mediaRepository.save(media);
    }

    /**
     * Deletes a Media from the database
     * @param mediaId the id of the Media to delete
     * @throws RuntimeException - Media doesn't exist or the id is null
     */
    public void deleteMedia (UUID mediaId) {
        checkMediaExistence(mediaId);
        mediaRepository.deleteById(mediaId);
    }

    /**
     * Checks whether the id is valid and the Media exists
     * @param mediaId the id of the Media to verify
     * @throws RuntimeException - Media doesn't exist
     */
    public void checkMediaExistence (UUID mediaId) throws RuntimeException {
        Optional<Media> m = mediaRepository.findById(mediaId);
        if(m.isEmpty()){
            throw new MediaNotFoundException("No media with the id " + mediaId + " could be found.");
        }
    }

    /**
     * Checks whether the id is valid and the Project exists
     * @param projectId the id of the Project to verify
     * @return the Project
     * @throws RuntimeException - Project doesn't exist
     *
     */
    public Project checkProjectExistence (UUID projectId) throws RuntimeException {
        Optional<Project> p = projectRepository.findById(projectId);
        if(p.isEmpty()){
            throw new ProjectNotFoundException("No project with the id " + projectId + "could be found.");
        }
        return p.get();
    }

    /**
     * Checks that a path is unique
     * @param path - the path to be added to the database
     * @throws RuntimeException - the path is already in use
     */
    public void checkPathUniqueness (String path) throws RuntimeException {
        if(mediaRepository.findAll().stream()
            .filter(x -> x.getPath().equals(path)).toList().size() > 0) {
            throw new IllegalArgumentException("");
        }
    }
}
