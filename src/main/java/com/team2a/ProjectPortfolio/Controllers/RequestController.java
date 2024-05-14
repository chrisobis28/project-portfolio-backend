package com.team2a.ProjectPortfolio.Controllers;


import com.team2a.ProjectPortfolio.Commons.Request;
import com.team2a.ProjectPortfolio.Exceptions.NotFoundException;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.RequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Routes.REQUESTS)
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
    @GetMapping("/{username}")
    public ResponseEntity<List<Request>> getRequestsForUser (@PathVariable(name="username") String username) {

        try{
            List<Request> requests = requestService.getRequestsForUser (username);
            return new ResponseEntity<>(requests, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    /**
     * Endpoint that retrieves all requests in the database
     * @return a list of requests
     */
    @GetMapping("/")
    public ResponseEntity<List<Request>> getRequests () {
        List<Request> requests = requestService.getRequests();
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<Request> addRequest (@PathVariable(name="projectId") UUID projectId,
                                               @RequestBody Request request) {
        try{
            Logger logger = LoggerFactory.getLogger(RequestController.class);
            logger.info(request.toString());
            Request r = requestService.addRequest(request, projectId);
            return new ResponseEntity<>(r, HttpStatus.CREATED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
