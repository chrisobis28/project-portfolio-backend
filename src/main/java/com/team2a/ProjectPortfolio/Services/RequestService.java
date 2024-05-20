package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.*;
import com.team2a.ProjectPortfolio.Repositories.*;
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

        List<Account> accounts = accountRepository
                .findAll()
                .stream()
                .filter(x -> x.getUsername().equals(username))
                .toList();

        if(accounts.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No account found with that username.");

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

        Account account = request.getAccount();

        if(!accountRepository.existsById(account.getUsername()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");

        if(account.hasRequestForProject(p.getProjectId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account already has a request for this project");


//        List<Request> requests = requestRepository
//                .findAll()
//                .stream()
//                .filter(x -> x.getNewTitle().equals(request.getNewTitle()) &&
//                            x.getNewDescription().equals(request.getNewDescription()) &&
//                            x.getNewBibtex().equals(request.getNewBibtex()) &&
//                            x.isCounterOffer() == request.isCounterOffer() &&
//                            x.getProject().equals(p) &&
//                            x.getMedia().equals(request.getMedia()) &&
//                            x.getLinks().equals(request.getLinks()) &&
//                            x.getTags().equals(request.getTags()) &&
//                            x.getCollaborators().equals(request.getCollaborators()))
//                .toList();

//        if(!requests.isEmpty())
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request already exists");
//        else {

//            //delete later - Just for testing

//            //You need to have these in the database in order to add this request to the db.
//            //In the final product, the client will send these to you and guarantee they
//            //  are in the db. ATM, you can not test this endpoint in isolation
//            // if you do not first add these to the db. To test with postman, uncomment below.

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

//            //delete later

        requestRepository.save(request);

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
    public void acceptRequest (UUID requestId) {
        Optional<Request> request = requestRepository.findById(requestId);

        if(request.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found.");

        Request r = request.get();
        Project p = r.getProject();
        if(r.getNewTitle() != null)
            p.setTitle(r.getNewTitle());
        if(r.getNewDescription() != null)
            p.setDescription(r.getNewDescription());
        if(r.getNewBibtex() != null)
            p.setBibtex(r.getNewBibtex());

        //Resolve other fields

        projectRepository.save(p);
        requestRepository.delete(r);
    }
}
