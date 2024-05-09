package com.team2a.ProjectPortfolio.Repositories;

import com.team2a.ProjectPortfolio.Commons.ProjectsToCollaborators;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ProjectsToCollaboratorsRepository extends JpaRepository<ProjectsToCollaborators, UUID> {
    @SuppressWarnings("checkstyle:MethodName")
    List<ProjectsToCollaborators> findAllByProjectProjectId (UUID projectId);
}
