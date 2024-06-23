package com.team2a.ProjectPortfolio.Repositories;
import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Commons.Request;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
@Repository
public interface RequestRepository extends JpaRepository<Request, UUID> {

    List<Request> findAllByAccount (Account account);

    @Modifying
    @Transactional
    @Query("DELETE FROM Request r WHERE r.requestId = ?1")
    void deleteByRequestId (UUID requestId);


}
