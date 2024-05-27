package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.*;
import com.team2a.ProjectPortfolio.Repositories.*;
import jakarta.transaction.Transactional;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.web.server.ResponseStatusException;

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

        Optional<Account> account = accountRepository.findById(username);

        if(account.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No account found with that username.");

        return account.get().getRequests();
    }

    /**
     * Method that gets all requests from the repository
     * @return List of requests
     */
    public List<Request> getRequests () {
        return requestRepository.findAll();
    }


    /**
     * Method for adding a request to the database
     * @param request the request to be added
     * @return the Request added or NotFoundException, if no project found
     */
    public Request addRequest (Request request) {
        Optional<Project> proj = projectRepository.findById(request.getProject().getProjectId());

        if(proj.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");

        Project p = proj.get();

        Optional<Account> account = accountRepository.findById(request.getAccount().getUsername());

        if(account.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");

        Account a = account.get();

        if(a.hasRequestForProject(p.getProjectId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Account already has a request for this project");


//            for (Media m : request.getMedia()) {
//                request.setMediaChanged(List.of(mediaRepository.save(m)));
//            }
//            for (Link l : request.getLinks()) {
//                request.setLinksChanged(List.of(linkRepository.save(l)));
//            }
//            for (Tag t : request.getTags()) {
//                request.setTagsChanged(List.of(tagRepository.save(t)));
//            }
//            for (Collaborator c : request.getCollaborators()) {
//                request.setCollaboratorsChanged(List.of(collaboratorRepository.save(c)));
//            }


        requestRepository.save(request);
        a.getRequests().add(request);
        accountRepository.save(a);
        return request;
    }

    /**
     * Get requests for a project
     * @param projectId the id of the project to be queried
     * @return the list of requests or exception if not found
     */
    public List<Request> getRequestsForProject (UUID projectId) {


        List<Project> projects = projectRepository
                .findAll()
                .stream()
                .filter(x -> x.getProjectId().equals(projectId))
                .toList();
        if(projects.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found.");

        Project p = projects.get(0);

        return p.getRequests();


    }

    /**
     * Method for deleting a specific request
     * @param requestId the id of the request to be deleted
     */
    public void deleteRequest (UUID requestId) {

        Optional<Request> request = requestRepository.findById(requestId);

        if(request.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found.");

        requestRepository.delete(request.get());
    }

    /**
     * Method for accepting a request
     * @param requestId the id of the request to be accepted
     */
    @Transactional
    public void acceptRequest (UUID requestId) throws Exception {
        Optional<Request> request = requestRepository.findById(requestId);

        if(request.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found.");

        Request r = request.get();
        Project p = r.getProject();
        if(r.getNewTitle() != null)
            p.setTitle(r.getNewTitle());
        if(r.getNewDescription() != null)
            p.setDescription(r.getNewDescription());

        //Resolve other fields

        projectRepository.save(p);
        requestRepository.delete(r);
    }
}
