package com.team2a.ProjectPortfolio.Services;

import com.team2a.ProjectPortfolio.Commons.Template;
import com.team2a.ProjectPortfolio.Commons.TemplateAddition;
import com.team2a.ProjectPortfolio.Repositories.TemplateAdditionRepository;
import com.team2a.ProjectPortfolio.Repositories.TemplateRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;

    private final TemplateAdditionRepository templateAdditionRepository;

    /**
     * Service for Template
     * @param templateRepository - the Template Repository
     * @param templateAdditionRepository - the Template Addition Repository (Medias and Links)
     */
    @Autowired
    public TemplateService (TemplateRepository templateRepository,
                            TemplateAdditionRepository templateAdditionRepository) {
        this.templateRepository = templateRepository;
        this.templateAdditionRepository = templateAdditionRepository;
    }

    /**
     * Creates a new Template entry in the database
     * @param template - the Template for creation
     * @return - the Template created
     * @throws RuntimeException - name of the Template is already in use
     */
    public Template createTemplate (Template template) throws RuntimeException {
        if (templateRepository.existsById(template.getTemplateName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Name already in use.");
        }
        return templateRepository.save(template);
    }

    /**
     * Deletes a Template entry from the database
     * @param templateName - the name of the Template to be deleted
     * @throws RuntimeException - Template can't be found
     */
    public void deleteTemplate (String templateName) throws RuntimeException {
        if (!templateRepository.existsById(templateName)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Template not found.");
        }
        templateRepository.deleteById(templateName);
    }

    /**
     * Gets a Template from the database
     * @param templateName - the name of the Template to be retrieved
     * @throws RuntimeException - Template can't be found
     * @return - the Template found
     */
    public Template getTemplateByName (String templateName) throws RuntimeException {
        return templateRepository.findById(templateName)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Template not found."));
    }

    /**
     * Gets all Templates currently stored
     * @return - the list of Templates
     */
    public List<Template> getAllTemplates () {
        return templateRepository.findAll();
    }

    public TemplateAddition addTemplateAddition (String templateName, TemplateAddition templateAddition) {
        templateAddition.setTemplate(templateRepository
            .findById(templateName)
            .orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Template not found.")));
        return templateAdditionRepository.save(templateAddition);
    }

    public void deleteTemplateAddition (UUID templateAdditionId) {
        if (!templateAdditionRepository.existsById(templateAdditionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Template Addition not found.");
        }
        templateAdditionRepository.deleteById(templateAdditionId);
    }

    public List<TemplateAddition> getAllTemplateAdditions (String templateName) {
        if (!templateRepository.existsById(templateName)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Template not found.");
        }
        return templateAdditionRepository.findAllByTemplate_TemplateName(templateName);
    }
}
