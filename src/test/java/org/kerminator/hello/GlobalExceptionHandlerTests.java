package org.kerminator.hello;

import org.junit.jupiter.api.Test;
import org.kerminator.hello.exception.GlobalExceptionHandler;
import org.kerminator.hello.exception.ProductNotFoundException;
import org.kerminator.hello.exception.ValidationErrorResponse;
import org.kerminator.hello.model.Product;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

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

    private static class ValidationMethodStub {
        void create(Product product) {
            // No-op stub used to create MethodParameter for MethodArgumentNotValidException.
        }
    }
}
