package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Collaborator;
import com.team2a.ProjectPortfolio.Commons.ProjectsToCollaborators;
import com.team2a.ProjectPortfolio.Repositories.ProjectsToCollaboratorsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CollaboratorService {
    private final transient ProjectsToCollaboratorsRepository projectsToCollaboratorsRepository;

    /**
     * Constructor for the collaborator service
     * @param projectsToCollaboratorsRepository the projectsToCollaborators Repository
     */
    @Autowired
    public CollaboratorService(ProjectsToCollaboratorsRepository projectsToCollaboratorsRepository) {
        this.projectsToCollaboratorsRepository = projectsToCollaboratorsRepository;
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
        List<ProjectsToCollaborators> projectsToCollaborators = projectsToCollaboratorsRepository.
                findAllByProject_ProjectId(projectId);
        List<Collaborator> collaborators = projectsToCollaborators.stream()
                .map(ProjectsToCollaborators::getCollaborator).collect(Collectors.toList());
        if(collaborators.isEmpty()) {
            throw new EntityNotFoundException();
        }
        return collaborators;
    }
}
