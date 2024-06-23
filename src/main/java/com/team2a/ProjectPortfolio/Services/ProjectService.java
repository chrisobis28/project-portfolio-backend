package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.ProjectsToAccounts;
import com.team2a.ProjectPortfolio.Commons.RoleInProject;
import com.team2a.ProjectPortfolio.Commons.Template;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectsToAccountsRepository;
import com.team2a.ProjectPortfolio.security.SecurityUtils;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    private final ProjectsToAccountsRepository projectsToAccountsRepository;

    private final SecurityUtils securityUtils;

    /**
     * Constructor for the Project Service
     * @param projectRepository - the Project Repository
     * @param securityUtils - the Security Utils
     * @param projectsToAccountsRepository - the Projects to Accounts Repository
     */
    @Autowired
    public ProjectService(ProjectRepository projectRepository,
                          SecurityUtils securityUtils,
                          ProjectsToAccountsRepository projectsToAccountsRepository) {
        this.projectRepository = projectRepository;
        this.securityUtils = securityUtils;
        this.projectsToAccountsRepository = projectsToAccountsRepository;
    }

    /**
     * Returns a list of all Projects
     * @return the list of all projects
     */
    public List<Project> getProjects () {
        return projectRepository.findAll();
    }

    /**
     * Deletes a project based on its ID
     * @param projectId the id of the project to be deleted
     */
    public void deleteProject (UUID projectId) {
        projectRepository.delete(projectRepository.findById(projectId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"))
        );
    }

    /**
     * Returns an updated project given an id and new features
     * @param projectId the id of the project
     * @param project the project updates to be persisted
     * @return the changed project with the specified ID
     */
    public Project updateProject (UUID projectId, Project project) {
        Project existingProject = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
        existingProject.setTitle(project.getTitle());
        existingProject.setDescription(project.getDescription());
        existingProject.setArchived(project.getArchived());
        existingProject = projectRepository.save(existingProject);
        return existingProject;
    }

    /**
     * Creates a new project
     * @param project the project to be created
     * @return the created project
     * @throws ResponseStatusException(409) if a project with the same name and description already exists
     */
    public Project createProject (Project project) {
        Optional<Project> existing = projectRepository.findFirstByTitleAndDescription(project.getTitle(),
                project.getDescription());
        if (existing.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Project with same name and description already exists");
        }
        Project result;
        if(project.getTemplate()==null){
            result = new Project(project.getTitle(), project.getDescription(), project.getArchived());
        } else{
            result = new Project(project.getTitle(), project.getDescription(), project.getArchived(), project.getTemplate());
        }
        ProjectsToAccounts pta = new ProjectsToAccounts(RoleInProject.PM, securityUtils.getCurrentUser(), result);
        result = projectRepository.save(result);
        projectsToAccountsRepository.save(pta);
        return result;
    }

    /**
     * Returns a project given an id
     * @param projectId the id of the project
     * @return a project queried by its id
     */
    public Project getProjectById (UUID projectId) {
        return projectRepository.findById(projectId).
            orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
    }

    /**
     * Checks if a user belongs to a project
     * @param username the username of the user
     * @param projectId the id of the project
     * @return the user's role in the project,
     * @throws ResponseStatusException(403) if the user does not belong to the project
     * @throws ResponseStatusException(404) if the project does not exist
     */
    public RoleInProject userBelongsToProject (String username, UUID projectId) {
        List<ProjectsToAccounts> ptaList = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"))
            .getProjectsToAccounts();

        return ptaList.stream()
            .filter(pta -> pta.getAccount().getUsername().equals(username))
            .map(ProjectsToAccounts::getRole)
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not belong to this project"));
    }

    /**
     * Updates the template of a project, this could be adding, updating or deleting the template
     * @param projectId the if of the project
     * @param template the new template
     * @return the project with the updated template
     */
    public Project updateProjectTemplate (UUID projectId, Template template) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
        project.setTemplate(template);
        project = projectRepository.save(project);
        return project;
    }

    /**
     * Remove the template of a project
     * @param projectId the id of the project
     * @return the project having the template set to null
     */
    public Project removeTemplateFromProject (UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
        project.setTemplate(null);
        project = projectRepository.save(project);
        return project;
    }

    /**
     * Retrieve the template of a project given the project id
     * @param projectId the id of the project
     * @return the template of the specific project
     */
    public Template getTemplateByProjectId (UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
        return project.getTemplate();
    }
}
