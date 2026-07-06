package org.kerminator.hello.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final String URI_PREFIX = "uri=";
        private static final String DEFAULT_VALIDATION_MESSAGE = "Invalid value";
        private static final String DEFAULT_GLOBAL_VALIDATION_MESSAGE = "Validation error";

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private String getSanitizedPath(WebRequest request) {
                String description = request.getDescription(false);
                String path = description.startsWith(URI_PREFIX)
                                ? description.substring(URI_PREFIX.length())
                                : description;
        return HtmlUtils.htmlEscape(path);
    }

        private ValidationErrorResponse buildErrorResponse(
                        HttpStatus status,
                        String error,
                        String message,
                        WebRequest request) {
                return new ValidationErrorResponse(
                                status.value(),
                                error,
                                message,
                                getSanitizedPath(request)
                );
        }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ValidationErrorResponse> handleProductNotFound(
            ProductNotFoundException ex, WebRequest request) {

        ValidationErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Product Not Found",
                ex.getMessage(),
                request
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {

        ValidationErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "Request validation failed",
                request
        );

        Map<String, List<String>> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            String message = fieldError.getDefaultMessage();
            if (message == null || message.isBlank()) {
                message = DEFAULT_VALIDATION_MESSAGE;
            }
            fieldErrors.computeIfAbsent(fieldError.getField(), ignored -> new ArrayList<>())
                    .add(message);
        }
        errorResponse.setFieldErrors(fieldErrors);

        List<String> globalErrors = ex.getBindingResult().getGlobalErrors()
                .stream()
                .map(error -> {
                    String message = error.getDefaultMessage();
                    return message == null || message.isBlank()
                            ? DEFAULT_GLOBAL_VALIDATION_MESSAGE
                            : message;
                })
                .toList();
        errorResponse.setGlobalErrors(globalErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, WebRequest request) {

        logger.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());

        ValidationErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.CONFLICT,
                "Conflict",
                "Request conflicts with the current state of the resource",
                request
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationErrorResponse> handleMessageNotReadable(
            HttpMessageNotReadableException ex, WebRequest request) {

        ValidationErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Malformed Request",
                "Request body is missing or not well-formed",
                request
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ValidationErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {

        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "expected type";
        String message = String.format("Parameter '%s' must be a valid %s",
                HtmlUtils.htmlEscape(ex.getName()), HtmlUtils.htmlEscape(requiredType));

        ValidationErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid Parameter",
                message,
                request
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {

        ValidationErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "Request validation failed",
                request
        );

        Map<String, List<String>> fieldErrors = new LinkedHashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String field = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            if (message == null || message.isBlank()) {
                message = DEFAULT_VALIDATION_MESSAGE;
            }
            fieldErrors.computeIfAbsent(field, ignored -> new ArrayList<>()).add(message);
        }
        errorResponse.setFieldErrors(fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ValidationErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {

        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        ValidationErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred",
                request
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}