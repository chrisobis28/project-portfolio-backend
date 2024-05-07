package com.team2a.ProjectPortfolio.Repositories;

import com.team2a.ProjectPortfolio.Commons.RequestLinkProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RequestLinkProjectRepository extends JpaRepository<RequestLinkProject, UUID> {
}
