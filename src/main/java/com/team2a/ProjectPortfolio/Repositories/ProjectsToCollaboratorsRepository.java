package com.team2a.ProjectPortfolio.Repositories;

import com.team2a.ProjectPortfolio.Commons.ProjectsToCollaborators;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectsToCollaboratorsRepository extends JpaRepository<ProjectsToCollaborators, UUID> {
}
