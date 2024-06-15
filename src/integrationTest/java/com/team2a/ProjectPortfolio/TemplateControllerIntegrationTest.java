package com.team2a.ProjectPortfolio;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2a.ProjectPortfolio.Commons.Template;
import com.team2a.ProjectPortfolio.Commons.TemplateAddition;
import com.team2a.ProjectPortfolio.Repositories.TemplateAdditionRepository;
import com.team2a.ProjectPortfolio.Repositories.TemplateRepository;
import com.team2a.ProjectPortfolio.security.SecurityConfigUtils;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters=false)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class TemplateControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private TemplateAdditionRepository templateAdditionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityConfigUtils securityConfigUtils;

    private Template template;

    private TemplateAddition ta1;

    @BeforeEach
    public void setUp() {
        templateRepository.deleteAll();
        templateAdditionRepository.deleteAll();

        template = new Template("name1", "description1", 5);
        templateRepository.save(template);

        templateRepository.save(new Template("proxy1", "description", 4));
        templateRepository.save(new Template("proxy2", "description", 4));

        ta1 = new TemplateAddition("name1_1", false);
        TemplateAddition ta2 = new TemplateAddition("name1_2", false);
        TemplateAddition ta3 = new TemplateAddition("name1_3", false);

        ta1.setTemplate(template);
        ta2.setTemplate(template);
        ta3.setTemplate(template);

        templateAdditionRepository.saveAll(List.of(ta1, ta2, ta3));
        securityConfigUtils.setAuthentication();
    }

    @Test
    public void createTemplate() throws Exception {
        assertEquals(3, templateRepository.count());
        mockMvc.perform(post(Routes.TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Template("name2", "description2", 6))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.templateName", is("name2")))
            .andExpect(jsonPath("$.standardDescription", is("description2")))
            .andExpect(jsonPath("$.numberOfCollaborators", is(6)));

        assertEquals(4, templateRepository.count());

        mockMvc.perform(post(Routes.TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(template)))
            .andExpect(status().isForbidden());

        mockMvc.perform(post(Routes.TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Template("name2", null, 6))))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteTemplate() throws Exception {
        assertEquals(3, templateRepository.count());

        mockMvc.perform(delete(Routes.TEMPLATE + "/" + "name3")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        assertEquals(3, templateRepository.count());

        mockMvc.perform(delete(Routes.TEMPLATE + "/" + template.getTemplateName())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        assertEquals(2, templateRepository.count());
    }

    @Test
    public void getTemplateByName() throws Exception {
        mockMvc.perform(get(Routes.TEMPLATE + "/" + template.getTemplateName())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.templateName", is("name1")))
            .andExpect(jsonPath("$.standardDescription", is("description1")))
            .andExpect(jsonPath("$.numberOfCollaborators", is(5)));

        mockMvc.perform(get(Routes.TEMPLATE + "/" + "name2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void getAllTemplates() throws Exception {
        mockMvc.perform(get(Routes.TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].templateName", is("name1")))
            .andExpect(jsonPath("$[1].templateName", is("proxy1")))
            .andExpect(jsonPath("$[2].templateName", is("proxy2")));
    }

    @Test
    public void addTemplateAddition() throws Exception {
        assertEquals(3, templateAdditionRepository.count());

        mockMvc.perform(post(Routes.TEMPLATE + "/additions/" + "name2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TemplateAddition("real_name", false))))
            .andExpect(status().isNotFound());

        mockMvc.perform(post(Routes.TEMPLATE + "/additions/" + template.getTemplateName())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TemplateAddition(null, false))))
            .andExpect(status().isBadRequest());

        mockMvc.perform(post(Routes.TEMPLATE + "/additions/" + template.getTemplateName())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TemplateAddition("real_name", false))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.templateAdditionName", is("real_name")))
            .andExpect(jsonPath("$.media", is(false)));

        assertEquals(4, templateAdditionRepository.count());
    }

    @Test
    public void deleteTemplateAddition() throws Exception {
        UUID id2 = UUID.randomUUID();
        while(id2.equals(ta1.getTemplateAdditionId())) {
            id2 = UUID.randomUUID();
        }

        mockMvc.perform(delete(Routes.TEMPLATE + "/additions/" + id2)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        mockMvc.perform(delete(Routes.TEMPLATE + "/additions/" + ta1.getTemplateAdditionId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void getAllTemplateAdditions() throws Exception {
        mockMvc.perform(get(Routes.TEMPLATE + "/additions/" + "name3")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        mockMvc.perform(get(Routes.TEMPLATE + "/additions/" + template.getTemplateName())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].templateAdditionName", is("name1_1")))
            .andExpect(jsonPath("$[1].templateAdditionName", is("name1_2")))
            .andExpect(jsonPath("$[2].templateAdditionName", is("name1_3")));
    }
}
