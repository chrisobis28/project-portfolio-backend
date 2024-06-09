package com.team2a.ProjectPortfolio.Repositories;

import com.team2a.ProjectPortfolio.Commons.Collaborator;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.UUID;
@Repository
public interface CollaboratorRepository extends JpaRepository<Collaborator, UUID> {
    List<Collaborator> findAllByName (String name);
    Optional<Collaborator> findByName (String name);
}
