package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Commons.Request;
import com.team2a.ProjectPortfolio.Exceptions.NotFoundException;
import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestService {

    @Autowired
    @Setter
    private  AccountRepository accountRepository;

    /**
     * Retrieves all requests made by a specific user
     * @param username the username to be queried
     * @return the list of requests corresponding to the username
     */
    public List<Request> getRequestsForUser (String username) {
        if(username == null)
            throw new NotFoundException();

        List<Account> accounts = accountRepository
                .findAll()
                .stream()
                .filter(x -> x.getUsername().equals(username))
                .toList();

        if(accounts.isEmpty())
            throw new NotFoundException();

        return  accounts
                .get(0)
                .getRequests();
    }
}
