package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Collaborator;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.ProjectsToCollaborators;
import com.team2a.ProjectPortfolio.Repositories.CollaboratorRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectsToCollaboratorsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CollaboratorService {
    private final transient ProjectsToCollaboratorsRepository projectsToCollaboratorsRepository;
    private final transient CollaboratorRepository collaboratorRepository;
    private final transient ProjectRepository projectRepository;

    /**
     * The constructor for the Collaborator Service
     * @param projectsToCollaboratorsRepository the projectToCollaboratorsRepository
     * @param collaboratorRepository the collaborator repository
     * @param projectRepository the project repository
     */
    @Autowired
    public CollaboratorService (ProjectsToCollaboratorsRepository projectsToCollaboratorsRepository,
                               CollaboratorRepository collaboratorRepository, ProjectRepository projectRepository) {
        this.projectsToCollaboratorsRepository = projectsToCollaboratorsRepository;
        this.collaboratorRepository = collaboratorRepository;
        this.projectRepository = projectRepository;
    }


    /**
     * Returns a list of Collaborators which are part of a certain projectId
     * @param projectId the projectId
     * @return the list of Collaborators
     */
    public List<Collaborator> getCollaboratorsByProjectId (UUID projectId) {
        if(projectId == null) {
            throw new IllegalArgumentException();
        }
        Project project = projectRepository.findById(projectId).orElseThrow(EntityNotFoundException::new);
        List<ProjectsToCollaborators> projectsToCollaborators = projectsToCollaboratorsRepository.
                findAllByProjectProjectId(projectId);
        List<Collaborator> collaborators = projectsToCollaborators.stream()
                .map(ProjectsToCollaborators::getCollaborator).collect(Collectors.toList());
        return collaborators;
    }

    /**
     * Adds a collaborator to a specified projectId.If the collaborator is already in the database, we just
     * link it to the project. Otherwise, we create it and link to the project.
     * @param projectId the projectId
     * @param collaboratorName the collaborator name
     * @return the collaborator entity
     */
    public Collaborator addCollaboratorToProject (UUID projectId, String collaboratorName) {
        if(projectId == null) {
            throw new IllegalArgumentException();
        }
        Project project = projectRepository.findById(projectId).orElseThrow(EntityNotFoundException::new);
        List<Collaborator> collaborators = collaboratorRepository.findAllByName(collaboratorName);
        if(collaborators.isEmpty()) {
            Collaborator collaborator = new Collaborator(collaboratorName);
            collaborator = collaboratorRepository.save(collaborator);
            ProjectsToCollaborators projectsToCollaborators = new ProjectsToCollaborators(project,collaborator);
            projectsToCollaborators = projectsToCollaboratorsRepository.save(projectsToCollaborators);
            return collaborator;
        }
        return collaborators.get(0);

    }

    /**
     * Changes the name of a collaborator
     * @param collaboratorId the collaborator ID
     * @param collaboratorName the new collaborator name
     * @return the collaborator entity
     */
    public Collaborator editCollaboratorOfProject (UUID collaboratorId, String collaboratorName) {
        if(collaboratorId == null) {
            throw new IllegalArgumentException();
        }
        Collaborator collaborator = collaboratorRepository.findById(collaboratorId).
                orElseThrow(EntityNotFoundException::new);
        collaborator.setName(collaboratorName);
        collaborator = collaboratorRepository.save(collaborator);
        return collaborator;
    }

    /**
     * Deletes a collaborator based on his ID
     * @param collaboratorId the collaborator ID
     * @return a string containing a response
     */
    public String deleteCollaborator (UUID collaboratorId) {
        if(collaboratorId == null) {
            throw new IllegalArgumentException();
        }
        Collaborator collaborator = collaboratorRepository.findById(collaboratorId).
                orElseThrow(EntityNotFoundException::new);
        collaboratorRepository.delete(collaborator);
        return "Deleted collaborator";
    }
}
