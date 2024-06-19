package com.team2a.ProjectPortfolio.Controllers;


import static com.team2a.ProjectPortfolio.Routes.hostLink;
import static com.team2a.ProjectPortfolio.security.Permissions.ADMIN_ONLY;
import static com.team2a.ProjectPortfolio.security.Permissions.IS_CREATOR_OR_PM_IN_PROJECT;
import static com.team2a.ProjectPortfolio.security.Permissions.PM_IN_PROJECT;
import static com.team2a.ProjectPortfolio.security.Permissions.USER_IN_PROJECT;

import com.team2a.ProjectPortfolio.Commons.Request;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.RequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Routes.REQUESTS)
@CrossOrigin(hostLink)
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
    @PreAuthorize(USER_IN_PROJECT)
    public ResponseEntity<Request> addRequest (@Valid @RequestBody Request request) {
        Request r = requestService.addRequest(request);
        return new ResponseEntity<>(r, HttpStatus.CREATED);
    }


    /**
     * Controller method for getting all requests for a project
     * @param projectID the id of the project
     * @return response entity with body as the list of requests
     */
    @GetMapping("/project/{projectId}")
    @PreAuthorize(PM_IN_PROJECT)
    public ResponseEntity<List<Request>> getRequestsForProject (@PathVariable(name = "projectId") UUID projectID) {
        List<Request> requests = requestService.getRequestsForProject(projectID);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    /**
     * controller method for deleting a request
     * @param requestId the id of the project to remove
     * @return response entity showing status of the removal
     */
    @DeleteMapping("/{requestId}")
    @PreAuthorize(IS_CREATOR_OR_PM_IN_PROJECT)
    public ResponseEntity<Void> deleteRequest (@PathVariable(name = "requestId") UUID requestId) {
        requestService.deleteRequest(requestId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Controller method for accepting a request
     * @param projectId the id of the project
     * @param requestId the id of the request
     * @return response entity showing status of the acceptance
     */
    @PostMapping("/{projectId}/{requestId}")
    @PreAuthorize(PM_IN_PROJECT)
    public ResponseEntity<Void> acceptRequest (@PathVariable(name = "projectId") UUID projectId,
                                               @PathVariable(name = "requestId") UUID requestId) {
        requestService.acceptRequest(requestId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
