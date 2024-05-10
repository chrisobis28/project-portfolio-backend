package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
