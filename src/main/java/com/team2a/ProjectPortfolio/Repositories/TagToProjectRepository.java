package com.team2a.ProjectPortfolio.Repositories;

import com.team2a.ProjectPortfolio.Commons.Tag;
import com.team2a.ProjectPortfolio.Commons.TagsToProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TagToProjectRepository extends JpaRepository<TagsToProject, UUID> {
}
