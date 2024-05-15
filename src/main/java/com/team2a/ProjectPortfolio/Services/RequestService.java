package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Account;
import com.team2a.ProjectPortfolio.Commons.Project;
import com.team2a.ProjectPortfolio.Commons.Request;
import com.team2a.ProjectPortfolio.CustomExceptions.NotFoundException;
import com.team2a.ProjectPortfolio.Repositories.AccountRepository;
import com.team2a.ProjectPortfolio.Repositories.ProjectRepository;
import com.team2a.ProjectPortfolio.Repositories.RequestRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RequestService {

    @Autowired
    @Setter
    private AccountRepository accountRepository;

    @Autowired
    @Setter
    private RequestRepository requestRepository;

    @Autowired
    @Setter
    private ProjectRepository projectRepository;

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

    /**
     * Method that gets all requests from the repository
     * @return List of requests
     */
    public List<Request> getRequests () {
        return requestRepository.findAll();
    }

    public List<Request> getRequestsForProject (UUID projectId) {

        if(projectId == null)
            throw new NotFoundException();

        List<Project> projects = projectRepository
                .findAll()
                .stream()
                .filter(x -> x.getProjectId().equals(projectId))
                .toList();
        if(projects.isEmpty())
            throw new NotFoundException();

        Project p = projects.get(0);

        return p.getRequests();


    }
}
