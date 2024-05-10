package com.team2a.ProjectPortfolio.Repositories;

import com.team2a.ProjectPortfolio.Commons.Media;
import com.team2a.ProjectPortfolio.Commons.ProjectsToCollaborators;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {

  List<Media> findAllByProjectProjectId (UUID projectId);
}
