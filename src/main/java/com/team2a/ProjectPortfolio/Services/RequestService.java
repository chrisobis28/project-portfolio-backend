package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.*;
import com.team2a.ProjectPortfolio.Exceptions.NotFoundException;
import com.team2a.ProjectPortfolio.Repositories.*;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

    @Autowired
    @Setter
    private RequestCollaboratorsProjectsRepository requestCollaboratorsProjectsRepository;

    @Autowired
    @Setter
    private RequestLinkProjectRepository requestLinkProjectRepository;

    @Autowired
    @Setter
    private RequestMediaProjectRepository requestMediaProjectRepository;

    @Autowired
    @Setter
    private RequestTagProjectRepository requestTagProjectRepository;

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


    public Request addRequest (Request request, UUID projectId) {
        Optional<Project> proj = projectRepository.findById(projectId);

//        if(proj.isEmpty())
//            throw new NotFoundException();
//
//        Project p = proj.get();

        Project p = new Project();
        projectRepository.save(p);

        List<Request> requests = requestRepository
                .findAll()
                .stream()
                .filter(x -> x.getNewTitle().equals(request.getNewTitle()) &&
                            x.getNewDescription().equals(request.getNewDescription()) &&
                            x.getNewBibtex().equals(request.getNewBibtex()) &&
                            x.isCounterOffer() == request.isCounterOffer() &&
                            x.getProject().equals(p) &&
                            x.getMedia().equals(request.getMedia()) &&
                            x.getLinks().equals(request.getLinks()) &&
                            x.getTags().equals(request.getTags()) &&
                            x.getCollaborators().equals(request.getCollaborators()))
                .toList();

        if(!requests.isEmpty())
            return requests.get(0);
        else {
            request.setProject(p);
            requestRepository.save(request);
            for (Media m : request.getMedia())
                requestMediaProjectRepository.save(new RequestMediaProject(request, m));
            for (Link l : request.getLinks())
                requestLinkProjectRepository.save(new RequestLinkProject(l, request));
            for (Tag t : request.getTags())
                requestTagProjectRepository.save(new RequestTagProject(request, t));
            for (Collaborator c : request.getCollaborators())
                requestCollaboratorsProjectsRepository.save(new RequestCollaboratorsProjects(request, c));
            return request;
        }
    }
}
