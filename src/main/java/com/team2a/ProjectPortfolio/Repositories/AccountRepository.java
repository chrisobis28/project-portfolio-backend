package com.team2a.ProjectPortfolio.Repositories;

import com.team2a.ProjectPortfolio.Commons.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
}
