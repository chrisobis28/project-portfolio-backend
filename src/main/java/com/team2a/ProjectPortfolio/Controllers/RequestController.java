package com.team2a.ProjectPortfolio.Controllers;


import com.team2a.ProjectPortfolio.Commons.Request;
import com.team2a.ProjectPortfolio.CustomExceptions.NotFoundException;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.RequestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

import static com.team2a.ProjectPortfolio.security.Permissions.*;

@RestController
@RequestMapping(Routes.REQUESTS)
@CrossOrigin("http://localhost:4200")
public class RequestController {

    private final RequestService requestService;

    /**
     * Constructor for the request Controller
     * @param requestService the instance of requestService used
     */
    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    /**
     * API mapping for the getRequestsForUser endpoint
     * Retrieves all requests which a user made
     * @param username the username that must be searched for
     * @return A list of requests corresponding to the specified username
     * or a Response with adequate error code
     */
    @GetMapping("/public/user/{username}")
    public ResponseEntity<List<Request>> getRequestsForUser (@PathVariable(name="username") String username) {
        List<Request> requests = requestService.getRequestsForUser (username);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }


    /**
     * Endpoint that retrieves all requests in the database
     * @return a list of requests
     */
    @GetMapping("/")
    @PreAuthorize(ADMIN_ONLY)
    public ResponseEntity<List<Request>> getRequests () {
        List<Request> requests = requestService.getRequests();
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    /**
     * Controller method which adds a request to the database
     * @param request The body of the request which we want to add
     * @return Response entity containing the request as a body.
     */

    @PutMapping("/")
    public ResponseEntity<Request> addRequest (@RequestBody Request request) {
        Request r = requestService.addRequest(request);
        return new ResponseEntity<>(r, HttpStatus.CREATED);
    }


    /**
     * Controller method for getting all requests for a project
     * @param projectId the id of the project
     * @return response entity with body as the list of requests
     */
    @GetMapping("/project/{projectId}")
    @PreAuthorize(PM_IN_PROJECT)
    public ResponseEntity<List<Request>> getRequestsForProject (@PathVariable(name = "projectId") UUID projectId) {
        List<Request> requests = requestService.getRequestsForProject(projectId);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    /**
     * Endpoint for deleting a request
     * @param requestId
     * @param projectId
     * @return void
     */
    @Transactional
    @PutMapping("/delete/{requestId}/{projectId}")
    @PreAuthorize(PM_IN_PROJECT)
    public ResponseEntity<Void> deleteRequest (@PathVariable(name = "requestId") UUID requestId,
                                               @PathVariable("projectId") UUID projectId) {
        requestService.deleteRequest(requestId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Controller method for accepting a request
     * @param projectId the id of the project
     * @param requestId the id of the request
     * @return response entity showing status of the acceptance
     */
    @PutMapping("/{projectId}/{requestId}")
    @PreAuthorize(PM_IN_PROJECT)
    public ResponseEntity<Void> acceptRequest (@PathVariable(name = "projectId") UUID projectId,
                                               @PathVariable(name = "requestId") UUID requestId) {
        requestService.acceptRequest(requestId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{requestId}/{projectId}")
    @PreAuthorize(PM_IN_PROJECT)
    public ResponseEntity<Request> getRequestById (@PathVariable("requestId") UUID requestId,
                                                   @PathVariable("projectId") UUID projectId) {
        try {
            Request body = requestService.getRequestById(requestId);
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
