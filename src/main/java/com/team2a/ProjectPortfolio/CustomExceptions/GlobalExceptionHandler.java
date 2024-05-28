package com.team2a.ProjectPortfolio.CustomExceptions;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.springframework.http.HttpStatus;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation exceptions
     * @param ex MethodArgumentNotValidException
     * @param request HttpServletRequest
     * @return ApiErrorResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiErrorResponse handleValidationExceptions (MethodArgumentNotValidException ex, HttpServletRequest request) {
        BindingResult result = ex.getBindingResult();
        Map<String, String> errors = result.getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                DefaultMessageSourceResolvable::getDefaultMessage));

        String errorMessage = errors.entrySet().stream()
            .map(entry -> "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"")
            .collect(Collectors.joining(", "));

        String formattedMessage = "{" + errorMessage + "}";

        return new ApiErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            formattedMessage,
            request.getRequestURI()
        );
    }

    public static class ApiErrorResponse {

        @Getter
        @Setter
        private LocalDateTime timestamp;

        @Getter
        @Setter
        private int status;

        @Getter
        @Setter
        private String error;

        @Getter
        @Setter
        private String message;

        @Getter
        @Setter
        private String path;

        /**
         * Constructor
         * @param timestamp LocalDateTime
         * @param status int
         * @param error String
         * @param message String
         * @param path String
         */
        public ApiErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
        }
    }
}
