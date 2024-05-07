package com.team2a.ProjectPortfolio.Repositories;

import com.team2a.ProjectPortfolio.Commons.RequestCollaboratorsProjects;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RequestCollaboratorsProjectsRepository extends JpaRepository<RequestCollaboratorsProjects, UUID> {
}
