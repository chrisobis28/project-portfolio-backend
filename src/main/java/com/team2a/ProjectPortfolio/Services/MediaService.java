package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Media;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.Request;
import com.team2a.ProjectPortfolio.Commons.RequestMediaProject;
import com.team2a.ProjectPortfolio.CustomExceptions.MediaNotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.NotFoundException;
import com.team2a.ProjectPortfolio.CustomExceptions.ProjectNotFoundException;
import com.team2a.ProjectPortfolio.Repositories.MediaRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.dto.MediaFileContent;
import java.io.File;
import java.util.*;

import com.team2a.ProjectPortfolio.Repositories.RequestMediaProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.RequestRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MediaService {

    private final MediaRepository mediaRepository;
    private final ProjectRepository projectRepository;
    private final RequestRepository requestRepository;
    private final RequestMediaProjectRepository requestMediaProjectRepository;
    @Setter
    private MediaHelper mediaHelper;

    /**
     * Constructor
     * @param mediaRepository
     * @param projectRepository
     * @param requestRepository
     * @param requestMediaProjectRepository
     */
    @Autowired
    public MediaService(MediaRepository mediaRepository, ProjectRepository projectRepository,
                        RequestRepository requestRepository, RequestMediaProjectRepository requestMediaProjectRepository) {
        this.mediaRepository = mediaRepository;
        this.projectRepository = projectRepository;
        mediaHelper = new MediaHelper();
        this.requestRepository = requestRepository;
        this.requestMediaProjectRepository = requestMediaProjectRepository;
    }

    /**
     * Returns all the Medias corresponding to a specific Project
     * @param projectId the project UUID
     * @return a List of Tuples that contain the media, the media name and the media description
     * @throws RuntimeException- Project doesn't exist or the id is null
     */
    public List<MediaFileContent> getImagesContentByProjectId (UUID projectId){
        //https://www.geeksforgeeks.org/spring-boot-file-handling/
        checkProjectExistence(projectId);
        List<Media> mediaToGetObject = mediaRepository.findAllByProjectProjectId(projectId);
        List<MediaFileContent> mediaFiles = new ArrayList<>();
        for (Media media : mediaToGetObject) {
            if (media != null) {
                mediaFiles.add(new MediaFileContent(media.getName(), media.getPath(),
                        mediaHelper.getFileContents(media.getPath()+projectId)));
            }
        }
        return mediaFiles;
    }
    /**
     * Gets the list of medias of a specific project
     * @param projectId the project of which we need to get the media
     * @return the list of media
     */
    public List<Media> getDocumentsByProjectId (UUID projectId){
        try {
            checkProjectExistence(projectId);
        }
        catch (ProjectNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        return mediaRepository.findAllByProjectProjectId(projectId);
    }

    /**
     * Gets the document from the backend
     * @param mediaId the mediaId of the document
     * @return a Pair containing the file contents and it's filename
     */
    public MediaFileContent getDocumentByMediaId (UUID mediaId){
        //https://www.geeksforgeeks.org/spring-boot-file-handling/
        Media media;
        try {
            media = checkMediaExistence(mediaId);
        }
        catch (MediaNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        Media mediaToGetObject = mediaRepository.findMediaByMediaId(mediaId);
        return new MediaFileContent(mediaToGetObject.getName(),mediaToGetObject.getPath(),
                mediaHelper.getFileContents(mediaToGetObject.getPath()+media.getProject().getProjectId()));
    }
    /**
     * Adds a Media to a specific Project
     * @param projectId the id of the Project that gets a new media
     * @param file the Media to be added
     * @param name the name of the media
     * @return the Media that was added
     * @throws RuntimeException - Project doesn't exist or the id is null
     */
    public Media addMediaToProject (UUID projectId, MultipartFile file,String name){
        Project p = null;
        try {
            p = checkProjectExistence(projectId);
        }
        catch (ProjectNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        checkPathUniqueness(file.getOriginalFilename()+projectId);
        String filePath = System.getProperty("user.dir") + "/assets" + File.separator + file.getOriginalFilename()+projectId;
        Media media = new Media(name,file.getOriginalFilename());
        media.setProject(p);
        //https://www.geeksforgeeks.org/spring-boot-file-handling/
        // Try block to check exceptions
        mediaHelper.saveFile(filePath,file);
        return mediaRepository.save(media);
    }


    /**
     * Deletes a Media from the database
     * @param mediaId the id of the Media to delete
     * @throws RuntimeException - Media doesn't exist or the id is null
     * @return returns the media deleted
     */
    public Media deleteMedia (UUID mediaId) throws RuntimeException {
        Media m = checkMediaExistence(mediaId);
        mediaHelper.deleteFile(System.getProperty("user.dir") + "/assets" + File.separator +
                m.getPath()+m.getProject().getProjectId());
        mediaRepository.deleteById(mediaId);
        return m;
    }

    /**
     * Checks whether the id is valid and the Media exists
     * @param mediaId the id of the Media to verify
     * @throws RuntimeException - Media doesn't exist
     * @return returns the media found
     */
    public Media checkMediaExistence (UUID mediaId) throws RuntimeException {
        Optional<Media> m = mediaRepository.findById(mediaId);
        if(m.isEmpty()){
            throw new MediaNotFoundException("No media with the id " + mediaId + " could be found.");
        }
        return m.get();
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
        if(!mediaRepository.findAll().stream()
                .filter(x -> (x.getPath()).equals(path)).toList().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Edits a Media in the database
     * @param media - the Media with all the new fields
     * @return - the Media that was edited
     */
    public Media editMedia (Media media) {
        Optional<Media> o = mediaRepository.findById(media.getMediaId());
        if(o.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return mediaRepository.save(media);
    }
    public Media changeFile (UUID mediaId,MultipartFile file){
        Media m = checkMediaExistence(mediaId);
        mediaHelper.deleteFile(System.getProperty("user.dir") + "/assets" + File.separator +
                m.getPath()+m.getProject().getProjectId());
        String filePath = System.getProperty("user.dir") + "/assets" + File.separator +
                file.getOriginalFilename()+m.getProject().getProjectId();
        mediaHelper.saveFile(filePath,file);
        m.setPath(file.getOriginalFilename());

        return mediaRepository.save(m);
    }

    public List<RequestMediaProject> getMediaForRequest (UUID requestId) {
        Request req = requestRepository.findById(requestId).orElseThrow(NotFoundException::new);
        return req.getRequestMediaProjects();
    }

    public Media addAddedMediaToRequest (UUID requestId, MultipartFile file,String name) {
        Request r = requestRepository.findById(requestId).orElseThrow(NotFoundException::new);
        checkPathUniqueness(file.getOriginalFilename());
        String filePath = System.getProperty("user.dir") + "/assets" + File.separator
                + file.getOriginalFilename()+ r.getProject().getProjectId();
        Media media = new Media(name,file.getOriginalFilename());
        mediaHelper.saveFile(filePath, file);
        RequestMediaProject body = new RequestMediaProject(r, media, false);
        mediaRepository.save(media);
        requestMediaProjectRepository.save(body);
        return media;
    }

    public Media addRemovedMediaToRequest (UUID requestId, UUID mediaId) {
        Request r = requestRepository.findById(requestId).orElseThrow(NotFoundException::new);
        Media m = mediaRepository.findById(mediaId).orElseThrow(NotFoundException::new);
        RequestMediaProject body = new RequestMediaProject(r, m, true);
        requestMediaProjectRepository.save(body);
        return m;
    }
}
