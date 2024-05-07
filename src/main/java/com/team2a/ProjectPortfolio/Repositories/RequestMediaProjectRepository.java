package com.team2a.ProjectPortfolio.Repositories;

import com.team2a.ProjectPortfolio.Commons.RequestMediaProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RequestMediaProjectRepository extends JpaRepository<RequestMediaProject, UUID> {
}
