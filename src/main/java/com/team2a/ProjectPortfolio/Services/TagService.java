package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.Tag;
import com.team2a.ProjectPortfolio.Commons.TagsToProject;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.TagRepository;
import com.team2a.ProjectPortfolio.Repositories.TagToProjectRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final TagToProjectRepository tagToProjectRepository;

    private final ProjectRepository projectRepository;

    /**
     * Constructor for the tag service
     *
     * @param tagRepository the tag repository
     * @param tagToProjectRepository the tag to project repository
     * @param projectRepository the project repository
     */
    @Autowired
    public TagService(TagRepository tagRepository, TagToProjectRepository tagToProjectRepository,
                      ProjectRepository projectRepository) {
        this.tagRepository = tagRepository;
        this.tagToProjectRepository = tagToProjectRepository;
        this.projectRepository = projectRepository;
    }

    /**
     * Create a tag
     *
     * @param tag the tag
     * @return the tag
     */
    public Tag createTag (Tag tag) {
        tagRepository.findByNameAndColor(tag.getName(), tag.getColor())
            .ifPresent(t -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Tag already exists");
            });
        return tagRepository.saveAndFlush(tag);
    }

    /**
     * Get all tags by project id
     *
     * @param projectId the project id
     * @return a list of tags
     */
    public List<Tag> getTagsByProjectId (UUID projectId) {
        projectRepository.findById(projectId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<TagsToProject> tagsToProjects = tagToProjectRepository.findAllByProjectProjectId(projectId);
        return tagsToProjects.stream()
            .map(TagsToProject::getTag).collect(java.util.stream.Collectors.toList());
    }

    /**
     * Add a tag to a project
     *
     * @param projectId the project id
     * @param tagId the tag id
     */
    public void addTagToProject (UUID projectId, UUID tagId) {
        Tag tag = tagRepository.findById(tagId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag does not exist"));
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project does not exist"));
        if (tagToProjectRepository.existsByProjectProjectIdAndTagTagId(projectId, tagId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tag already exists in project");
        }
        TagsToProject tagsToProject = new TagsToProject(tag, project);
        tagToProjectRepository.saveAndFlush(tagsToProject);
    }

    /**
     * Edit a tag
     *
     * @param tag the tag
     * @return the tag
     */
    public Tag editTag (Tag tag) {
        tagRepository.findById(tag.getTagId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return tagRepository.saveAndFlush(tag);
    }

    /**
     * Delete a tag
     *
     * @param tagId the tag id
     */
    public void deleteTag (UUID tagId) {
        if(!tagRepository.existsById(tagId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        tagRepository.deleteById(tagId);
    }

    /**
     * Remove a tag from a project
     *
     * @param projectId the project id
     * @param tagId the tag id
     */
    @Transactional
    public void removeTagFromProject (UUID projectId, UUID tagId) {
        if(!tagRepository.existsById(tagId) || !projectRepository.existsById(projectId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag or project does not exist");
        }
        if(!tagToProjectRepository.existsByProjectProjectIdAndTagTagId(projectId, tagId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag does not belong to project");
        }
        tagToProjectRepository.deleteByProjectProjectIdAndTagTagId(projectId, tagId);
    }
}
