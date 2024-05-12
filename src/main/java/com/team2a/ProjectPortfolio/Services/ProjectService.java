package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
     * Instantiates a new project and returns it
     * @param project A json deserialized object with the attributes for the project
     * @return the project added
     */
    public Project createProject (Project project) {
        if (project == null) {
            throw new IllegalArgumentException();
        }
        Optional<Project> existing = projectRepository.findFirstByTitleAndDescriptionAndBibtex(project.getTitle(),
                project.getDescription(), project.getBibtex());
        if (existing.isPresent()) {
            return existing.get();
        }
        Project result = new Project(project.getTitle(), project.getDescription(),
                project.getBibtex(), project.getArchived());
        result = projectRepository.save(result);
        return result;
    }
}
