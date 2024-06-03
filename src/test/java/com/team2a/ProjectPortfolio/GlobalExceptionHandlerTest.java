package com.team2a.ProjectPortfolio;

import com.team2a.ProjectPortfolio.CustomExceptions.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;


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
        BindingResult bindingResult = new BindException(new Object(), "objectName");
        bindingResult.addError(new FieldError("objectName", "name", "Name must be specified"));
        bindingResult.addError(new FieldError("objectName", "color", "Color must be specified"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        when(request.getRequestURI()).thenReturn("/tag/create");

        GlobalExceptionHandler.ApiErrorResponse response = exceptionHandler.handleValidationExceptions(ex, request);

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getError()).isEqualTo("Bad Request");
        assertThat(response.getMessage()).contains("\"name\":\"Name must be specified\"");
        assertThat(response.getMessage()).contains("\"color\":\"Color must be specified\"");
        assertThat(response.getPath()).isEqualTo("/tag/create");
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    public void whenResponseStatusException_thenReturnsProperResponseEntity() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        when(request.getRequestURI()).thenReturn("/project/1");

        ResponseEntity<GlobalExceptionHandler.ApiErrorResponse> responseEntity = exceptionHandler.handleResponseStatusException(ex, request);

        GlobalExceptionHandler.ApiErrorResponse response = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getError()).isEqualTo("404 NOT_FOUND \"Resource not found\"");
        assertThat(response.getMessage()).isEqualTo("Resource not found");
        assertThat(response.getPath()).isEqualTo("/project/1");
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    public void whenAccessDeniedException_thenReturnsForbiddenResponse() {
        AccessDeniedException ex = new AccessDeniedException("You do not have permission to access this resource");
        when(request.getRequestURI()).thenReturn("/protected/resource");

        ResponseEntity<GlobalExceptionHandler.ApiErrorResponse> responseEntity = exceptionHandler.handleAccessDeniedException(ex, request);

        GlobalExceptionHandler.ApiErrorResponse response = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(response.getError()).isEqualTo("Access Denied");
        assertThat(response.getMessage()).isEqualTo("You do not have permission to access this resource");
        assertThat(response.getPath()).isEqualTo("/protected/resource");
        assertThat(response.getTimestamp()).isNotNull();
    }
}
