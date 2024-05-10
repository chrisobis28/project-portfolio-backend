package com.team2a.ProjectPortfolio.Repositories;

import com.team2a.ProjectPortfolio.Commons.RequestLinkProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface RequestLinkProjectRepository extends JpaRepository<RequestLinkProject, UUID> {
}
