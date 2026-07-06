package org.kerminator.hello;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.kerminator.hello.exception.GlobalExceptionHandler;
import org.kerminator.hello.exception.ProductNotFoundException;
import org.kerminator.hello.exception.ValidationErrorResponse;
import org.kerminator.hello.model.Product;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTests {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleProductNotFound_returns404AndExpectedBody() {
        WebRequest request = new ServletWebRequest(new MockHttpServletRequest("GET", "/api/products/123"));

        ResponseEntity<ValidationErrorResponse> response =
                exceptionHandler.handleProductNotFound(new ProductNotFoundException(123L), request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Product Not Found", response.getBody().getError());
        assertEquals("Product not found with id: 123", response.getBody().getMessage());
        assertEquals("/api/products/123", response.getBody().getPath());
    }

    @Test
    void handleValidationException_usesDefaultMessagesForNullOrBlankMessages() throws Exception {
        Product target = new Product();
        target.setName(" ");
        target.setPrice(BigDecimal.ZERO);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "product");
        bindingResult.addError(new FieldError("product", "name", null, false, null, null, null));
        bindingResult.addError(new FieldError("product", "price", null, false, null, null, " "));
        bindingResult.addError(new ObjectError("product", null));

        Method method = ValidationMethodStub.class.getDeclaredMethod("create", Product.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        WebRequest request = new ServletWebRequest(new MockHttpServletRequest("POST", "/api/products"));
        ResponseEntity<ValidationErrorResponse> response = exceptionHandler.handleValidationException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        ValidationErrorResponse body = response.getBody();
        assertNotNull(body.getFieldErrors());
        assertEquals(List.of("Invalid value"), body.getFieldErrors().get("name"));
        assertEquals(List.of("Invalid value"), body.getFieldErrors().get("price"));

        assertNotNull(body.getGlobalErrors());
        assertEquals(List.of("Validation error"), body.getGlobalErrors());
    }

    @Test
    void handleGenericException_sanitizesPathInResponse() {
        WebRequest request = new ServletWebRequest(
                new MockHttpServletRequest("GET", "/api/products/<script>alert(1)</script>"));

        ResponseEntity<ValidationErrorResponse> response =
                exceptionHandler.handleGenericException(new RuntimeException("boom"), request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());

        String path = response.getBody().getPath();
        assertNotNull(path);
        assertTrue(path.contains("&lt;script&gt;"));
        assertFalse(path.contains("<script>"));
    }

    @Test
    void handleDataIntegrityViolation_returns409() {
        WebRequest request = new ServletWebRequest(new MockHttpServletRequest("POST", "/api/products"));

        ResponseEntity<ValidationErrorResponse> response = exceptionHandler.handleDataIntegrityViolation(
                new DataIntegrityViolationException("duplicate key value violates unique constraint"),
                request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Conflict", response.getBody().getError());
        assertEquals("Request conflicts with the current state of the resource", response.getBody().getMessage());
        assertEquals("/api/products", response.getBody().getPath());
    }

    @Test
    void handleMessageNotReadable_returns400() {
        WebRequest request = new ServletWebRequest(new MockHttpServletRequest("POST", "/api/products"));
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException(
                "Unexpected end-of-input",
                new MockHttpInputMessage(new byte[0]));

        ResponseEntity<ValidationErrorResponse> response = exceptionHandler.handleMessageNotReadable(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Malformed Request", response.getBody().getError());
        assertEquals("Request body is missing or not well-formed", response.getBody().getMessage());
    }

    @Test
    void handleTypeMismatch_returns400WithEscapedParameterName() throws Exception {
        WebRequest request = new ServletWebRequest(new MockHttpServletRequest("GET", "/api/products/abc"));

        Method method = ValidationMethodStub.class.getDeclaredMethod("findById", Long.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "abc", Long.class, "<id>", methodParameter, new IllegalArgumentException("nope"));

        ResponseEntity<ValidationErrorResponse> response = exceptionHandler.handleTypeMismatch(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid Parameter", response.getBody().getError());
        String message = response.getBody().getMessage();
        assertTrue(message.contains("&lt;id&gt;"));
        assertFalse(message.contains("<id>"));
        assertTrue(message.contains("Long"));
    }

    @Test
    void handleConstraintViolation_returns400WithFieldErrors() {
        Validator validator;
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        Set<ConstraintViolation<ConstraintBean>> violations = validator.validate(new ConstraintBean());
        ConstraintViolationException ex = new ConstraintViolationException(violations);

        WebRequest request = new ServletWebRequest(new MockHttpServletRequest("GET", "/api/products"));
        ResponseEntity<ValidationErrorResponse> response = exceptionHandler.handleConstraintViolation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation Failed", response.getBody().getError());
        assertNotNull(response.getBody().getFieldErrors());
        assertFalse(response.getBody().getFieldErrors().isEmpty());
        assertTrue(response.getBody().getFieldErrors().containsKey("name"));
    }

    private static class ValidationMethodStub {
        void create(Product product) {
            // No-op stub used to create MethodParameter for MethodArgumentNotValidException.
        }

        void findById(Long id) {
            // No-op stub used to create MethodParameter for MethodArgumentTypeMismatchException.
        }
    }

    private static class ConstraintBean {
        @NotBlank
        private String name = "";
    }
}
