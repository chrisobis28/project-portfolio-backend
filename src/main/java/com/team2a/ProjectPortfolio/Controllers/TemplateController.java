package com.team2a.ProjectPortfolio.Controllers;

import static com.team2a.ProjectPortfolio.Routes.hostLink;
import static com.team2a.ProjectPortfolio.security.Permissions.ADMIN_ONLY;
import static com.team2a.ProjectPortfolio.security.Permissions.PM_ONLY;

import com.team2a.ProjectPortfolio.Commons.Template;
import com.team2a.ProjectPortfolio.Commons.TemplateAddition;
import com.team2a.ProjectPortfolio.Routes;
import com.team2a.ProjectPortfolio.Services.TemplateService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Routes.TEMPLATE)
@CrossOrigin(hostLink)
public class TemplateController {

    private final TemplateService templateService;

    /**
     * Controller for Template
     * @param templateService - the Template Service
     */
    @Autowired
    public TemplateController (TemplateService templateService) {
        this.templateService = templateService;
    }

    /**
     * Creates a Template
     * @param template - the Template with the necessary info
     * @return - the created Template
     */
    @PostMapping("")
    @PreAuthorize(PM_ONLY)
    public ResponseEntity<Template> createTemplate (@Valid @RequestBody Template template) {
        return ResponseEntity.status(HttpStatus.OK).body(templateService.createTemplate(template));
    }

    /**
     * Deletes a Template
     * @param templateName - the name of the Template to be deleted
     * @return - deletion status
     */
    @DeleteMapping("/{templateName}")
    @PreAuthorize(ADMIN_ONLY)
    public ResponseEntity<Void> deleteTemplate (@PathVariable("templateName") String templateName) {
        templateService.deleteTemplate(templateName);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Retrieves a Template
     * @param templateName - the name of the Template to be retrieved
     * @return - the Template searched
     */
    @GetMapping("/{templateName}")
    @PreAuthorize(PM_ONLY)
    public ResponseEntity<Template> getTemplateByName (@PathVariable("templateName") String templateName) {
        return ResponseEntity.status(HttpStatus.OK).body(templateService.getTemplateByName(templateName));
    }

    /**
     * Retrieves all Templates in the database
     * @return - the list of Templates
     */
    @GetMapping("")
    @PreAuthorize(PM_ONLY)
    public ResponseEntity<List<Template>> getAllTemplates () {
        return ResponseEntity.status(HttpStatus.OK).body(templateService.getAllTemplates());
    }

    @PostMapping("/additions/{templateName}")
    @PreAuthorize(PM_ONLY)
    public ResponseEntity<TemplateAddition> addTemplateAddition (@PathVariable("templateName") String templateName,
                                                                @Valid @RequestBody TemplateAddition templateAddition) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(templateService.addTemplateAddition(templateName, templateAddition));
    }

    @DeleteMapping("/additions/{templateAdditionId}")
    public ResponseEntity<Void> deleteTemplateAddition (@PathVariable("templateAdditionId") UUID templateAdditionId) {
        templateService.deleteTemplateAddition(templateAdditionId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/additions/{templateName}")
    @PreAuthorize(PM_ONLY)
    public ResponseEntity<List<TemplateAddition>> getAllTemplateAdditions (
        @PathVariable("templateName") String templateName) {
        return ResponseEntity.status(HttpStatus.OK).body(templateService.getAllTemplateAdditions(templateName));
    }

}
