package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.*;
import com.team2a.ProjectPortfolio.CustomExceptions.NotFoundException;
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


    /**
     * Method for adding a request to the database
     * @param request the request to be added
     * @param projectId the id of the project changed in a request
     * @return the Request added or NotFoundException, if no project found
     */
    public Request addRequest (Request request, UUID projectId) {
        Optional<Project> proj = projectRepository.findById(projectId);

        if(proj.isEmpty())
            throw new NotFoundException();

        Project p = proj.get();

        //Project p = new Project();
        //projectRepository.save(p);

        // if you want to test in isolation, uncomment lines above and comment the three lines above it

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
