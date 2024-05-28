package com.team2a.ProjectPortfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2a.ProjectPortfolio.CustomExceptions.GlobalExceptionHandler.ApiErrorResponse;
import com.team2a.ProjectPortfolio.Repositories.TagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class GlobalExceptionHandlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void whenInvalidInput_thenReturnsBadRequest() throws Exception {
        // Prepare an invalid request body (e.g., missing required fields)
        String invalidRequestBody = "{}";

        MvcResult result = mockMvc.perform(post("/tag/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestBody))
            .andExpect(status().isBadRequest())
            .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        // Deserialize the response body into an ApiErrorResponse object
        ApiErrorResponse apiErrorResponse = objectMapper.readValue(jsonResponse, ApiErrorResponse.class);

        // Assert the fields in the ApiErrorResponse object
        assertThat(apiErrorResponse.getStatus()).isEqualTo(400);
        assertThat(apiErrorResponse.getError()).isEqualTo("Bad Request");
        assertThat(apiErrorResponse.getMessage()).contains("name", "Name must be specified");
        assertThat(apiErrorResponse.getMessage()).contains("color", "Color must be specified");
        assertThat(apiErrorResponse.getPath()).isEqualTo("/tag/create");
    }
}
