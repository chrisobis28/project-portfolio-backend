package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Collaborator;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.ProjectsToCollaborators;
import com.team2a.ProjectPortfolio.Repositories.CollaboratorRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectsToCollaboratorsRepository;
import com.team2a.ProjectPortfolio.dto.CollaboratorTransfer;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CollaboratorService {
    private final ProjectsToCollaboratorsRepository projectsToCollaboratorsRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final ProjectRepository projectRepository;

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
    public List<CollaboratorTransfer> getCollaboratorsByProjectId (UUID projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found")
        );
        List<ProjectsToCollaborators> projectsToCollaborators = projectsToCollaboratorsRepository.
                findAllByProjectProjectId(projectId);
        return projectsToCollaborators.stream()
            .map(ptc -> {
                Collaborator collaborator = ptc.getCollaborator();
                return new CollaboratorTransfer(collaborator.getCollaboratorId(), collaborator.getName(), ptc.getRole());
            })
            .collect(Collectors.toList());
    }

    /**
     * Looks if a collaborator with the given name exists.
     * If it exists, returns that collaborator, else adds one
     * and returns it
     * @param name the name of the collaborator to be added
     * @return the collaborator added
     */
    public Collaborator addCollaborator (String name) {
        List<Collaborator> collaborators = collaboratorRepository.findAllByName(name);

        if(collaborators.size() != 0)
            return collaborators.get(0);
        else
            return collaboratorRepository.save(new Collaborator(name));
    }

    /**
     * Changes the name of a collaborator
     * @param collaboratorId the collaborator ID
     * @param collaboratorName the new collaborator name
     * @return the collaborator entity
     */
    public Collaborator editCollaboratorOfProject (UUID collaboratorId, String collaboratorName) {
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
        Collaborator collaborator = collaboratorRepository.findById(collaboratorId).
                orElseThrow(EntityNotFoundException::new);
        collaboratorRepository.delete(collaborator);
        return "Deleted collaborator";
    }

    /**
     * Deletes a collaborator from a project based on his collaborator id and the project id
     * @param projectId the project ID
     * @param collaboratorId the collaborator ID
     * @return  a string containing a response
     */
    public String deleteCollaboratorFromProject (UUID projectId,UUID collaboratorId) {
        Collaborator collaborator = collaboratorRepository.findById(collaboratorId).
                orElseThrow(EntityNotFoundException::new);
        Project project = projectRepository.findById(projectId).
                orElseThrow(EntityNotFoundException::new);
        List<ProjectsToCollaborators> projectsToCollaboratorsList = projectsToCollaboratorsRepository.
                findAllByProjectProjectIdAndCollaboratorCollaboratorId(projectId,collaboratorId);
        projectsToCollaboratorsRepository.deleteAll(projectsToCollaboratorsList);

        return "Deleted collaborator";
    }

    /**
     * gets all collaborators
     * @return a list of all the collaborators
     */
    public List<Collaborator> getAllCollaborators () {
        return collaboratorRepository.findAll();
    }

    /**
     * Creates a new collaborator and adds it to a project
     * @param projectId the project ID
     * @param collaboratorTransfer the collaborator transfer object
     * @return the collaborator transfer object
     */
    public CollaboratorTransfer createAndAddCollaboratorToProject
    (UUID projectId, CollaboratorTransfer collaboratorTransfer) {
        Project p = projectRepository.findById(projectId).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND,"Project not found"));
        String role = collaboratorTransfer.getRole();
        Optional<Collaborator> collaboratorOptional = collaboratorRepository.findByName(collaboratorTransfer.getName());
        if (collaboratorOptional.isPresent()) {
            collaboratorTransfer.setCollaboratorId(collaboratorOptional.get().getCollaboratorId());
            if(projectsToCollaboratorsRepository
                .existsByProjectProjectIdAndCollaboratorCollaboratorId(p.getProjectId(),
                    collaboratorTransfer.getCollaboratorId())) {
                return collaboratorTransfer;
            }
            projectsToCollaboratorsRepository.save(new ProjectsToCollaborators(p, collaboratorOptional.get(), role));
        } else {
            Collaborator collaborator = collaboratorRepository.save(new Collaborator(collaboratorTransfer.getName()));
            collaboratorTransfer.setCollaboratorId(collaborator.getCollaboratorId());
            ProjectsToCollaborators ptc = new ProjectsToCollaborators(p, collaborator, role);
            projectsToCollaboratorsRepository.save(ptc);
        }
        return collaboratorTransfer;
    }
}
