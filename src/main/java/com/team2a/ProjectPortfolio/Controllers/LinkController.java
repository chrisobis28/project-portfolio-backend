package com.team2a.ProjectPortfolio.Controllers;


import com.team2a.ProjectPortfolio.Commons.Link;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.LinkService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Routes.LINK)
public class LinkController {
    private final LinkService linkService;

    /**
     * The constructor for the LinkController
     * @param linkService the Link Service
     */
    @Autowired
    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    /**
     * Edit the link of the project
     * @param link the link entity
     * @return the new link entity
     */
    @PutMapping("/")
    public ResponseEntity<Link> editLinkOfProject (@RequestBody Link link) {
        try {
            Link updatedLink = linkService.editLinkOfProject(link);
            return ResponseEntity.ok(updatedLink);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
