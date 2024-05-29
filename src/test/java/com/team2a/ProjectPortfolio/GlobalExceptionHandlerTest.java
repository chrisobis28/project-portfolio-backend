package com.team2a.ProjectPortfolio;

import com.team2a.ProjectPortfolio.CustomExceptions.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
    }

    @Test
    public void whenValidationException_thenReturnsBadRequestResponse() {
        // Arrange
        BindingResult bindingResult = new BindException(new Object(), "objectName");
        bindingResult.addError(new FieldError("objectName", "name", "Name must be specified"));
        bindingResult.addError(new FieldError("objectName", "color", "Color must be specified"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        when(request.getRequestURI()).thenReturn("/tag/create");

        // Act
        GlobalExceptionHandler.ApiErrorResponse response = exceptionHandler.handleValidationExceptions(ex, request);

        // Assert
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getError()).isEqualTo("Bad Request");
        assertThat(response.getMessage()).contains("\"name\":\"Name must be specified\"");
        assertThat(response.getMessage()).contains("\"color\":\"Color must be specified\"");
        assertThat(response.getPath()).isEqualTo("/tag/create");
        assertThat(response.getTimestamp()).isNotNull();
    }
}
