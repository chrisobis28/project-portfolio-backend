package com.team2a.ProjectPortfolio.Repositories;
import com.team2a.ProjectPortfolio.Commons.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface RequestRepository extends JpaRepository<Request, UUID> {
}
