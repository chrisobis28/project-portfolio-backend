package com.team2a.ProjectPortfolio.Repositories;

import com.team2a.ProjectPortfolio.Commons.ProjectsToCollaborators;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ProjectsToCollaboratorsRepository extends JpaRepository<ProjectsToCollaborators, UUID> {
    List<ProjectsToCollaborators> findAllByProjectProjectId (UUID projectId);
    List<ProjectsToCollaborators> findAllByProjectProjectIdAndCollaboratorCollaboratorId
            (UUID projectId, UUID collaboratorId);
}
