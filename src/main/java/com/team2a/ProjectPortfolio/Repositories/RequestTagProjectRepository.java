package com.team2a.ProjectPortfolio.Repositories;

import com.team2a.ProjectPortfolio.Commons.RequestTagProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RequestTagProjectRepository extends JpaRepository<RequestTagProject, UUID> {
}
