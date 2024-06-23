package com.team2a.ProjectPortfolio.Repositories;

import com.team2a.ProjectPortfolio.Commons.TagsToProject;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TagToProjectRepository extends JpaRepository<TagsToProject, UUID> {

    boolean existsByProjectProjectIdAndTagTagId (UUID projectId, UUID tagId);

    List<TagsToProject> findAllByProjectProjectId (UUID projectId);

    List<TagsToProject> findAllByProjectProjectIdAndTagTagId (UUID projectId, UUID tagId);

    void deleteByProjectProjectIdAndTagTagId (UUID projectId, UUID tagId);
}
