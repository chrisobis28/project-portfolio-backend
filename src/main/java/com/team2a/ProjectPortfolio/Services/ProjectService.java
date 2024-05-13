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
     * Deletes a project based on its ID
     * @param projectId the id of the project to be deleted
     * @return a string stating that the deletion was successful
     */
    public String deleteProject (UUID projectId) {
        if(projectId == null) {
            throw new IllegalArgumentException();
        }
        Project project = projectRepository.findById(projectId).orElseThrow(EntityNotFoundException::new);
        projectRepository.delete(project);
        return "Deleted project with specified ID";
    }
}
