package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    /**
     * The constructor for the Project Service
     * @param projectRepository the repository of projects
     */
    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Returns a list of all Projects
     * @return the list of all projects
     */
    public List<Project> getProjects () {
        List<Project> projects = projectRepository.findAll();
        return projects;
    }

    /**
     * Returns an updated project given an id and new features
     * @param projectId the id of the project
     * @param project the project updates to be persisted
     * @return the changed project with the specified ID
     */
    public Project updateProject (UUID projectId, Project project) {
        if (projectId == null) {
            throw new IllegalArgumentException();
        }
        Project existingProject = projectRepository.findById(projectId).orElseThrow(EntityNotFoundException::new);
        existingProject.setTitle(project.getTitle());
        existingProject.setDescription(project.getDescription());
        existingProject.setBibtex(project.getBibtex());
        existingProject.setArchived(project.getArchived());
        existingProject = projectRepository.save(existingProject);
        return existingProject;
    }
}
