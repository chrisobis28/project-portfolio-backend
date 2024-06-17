package com.team2a.ProjectPortfolio.Repositories;

import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.ProjectsToAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ProjectsToAccountsRepository extends JpaRepository<ProjectsToAccounts, UUID> {

    List<ProjectsToAccounts> findAllByAccountUsername(String username);
}
