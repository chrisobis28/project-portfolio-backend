package com.team2a.ProjectPortfolio;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2a.ProjectPortfolio.Commons.Template;
import com.team2a.ProjectPortfolio.Repositories.TemplateAdditionRepository;
import com.team2a.ProjectPortfolio.Repositories.TemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TemplateControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private TemplateAdditionRepository templateAdditionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Template template;

    @BeforeEach
    public void setUp() {
        templateRepository.deleteAll();
        templateAdditionRepository.deleteAll();

        template = new Template("name1", "description1", "bibtex1", 5);
        templateRepository.save(template);
    }

    @Test
    public void createTemplate() throws Exception {
        mockMvc.perform(post(Routes.TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Template("name2", "description2", "bibtex2", 6))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.templateName", is("name2")))
            .andExpect(jsonPath("$.standardDescription", is("description2")))
            .andExpect(jsonPath("$.standardBibtex", is("bibtex2")))
            .andExpect(jsonPath("$.numberOfCollaborators", is(6)));

        mockMvc.perform(post(Routes.TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(template)))
            .andExpect(status().isForbidden());

        mockMvc.perform(post(Routes.TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Template("name2", null, "bibtex2", 6))))
            .andExpect(status().isBadRequest());
    }
}
