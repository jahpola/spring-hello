package org.kerminator.hello;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kerminator.hello.controllers.ProductController;
import org.kerminator.hello.service.ProductService;
import org.kerminator.hello.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.mockito.Mockito;

import java.math.BigDecimal;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(ProductController.class)
class HelloControllerTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @Test
    void shouldCreateProduct() throws Exception {

        Product product = Product.builder()
                .name("nakki")
                .description("nakki teline")
                .price(BigDecimal.valueOf(10.15))
                .build();

        mvc.perform(MockMvcRequestBuilders
                .post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated());
                //.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("nakki"));
                //.andExpect(jsonPath("$.price").value(10.15));
    }

     @Test
     void find_existingProduct() throws Exception {

         Product product = Product.builder()
                 .id(100L)
                 .name("nakki")
                 .description("nakki teline")
                 .price(BigDecimal.valueOf(10.15))
                 .build();

         productService.saveProduct(product);

         mvc.perform(get("/api/products/{id}", "100"))
                 .andExpect(status().isOk());
     }

     @Test
     void findAllProducts() throws Exception {
         Product product = Product.builder()
                 .name("nakki")
                 .description("nakki teline")
                 .price(BigDecimal.valueOf(10.15))
                 .build();

         productService.saveProduct(product);

         mvc.perform(get("/api/products")).andExpect(status().isOk());
     }

    @Test
    void find_nonExistingProduct() throws Exception {
        mvc.perform(get("/api/products/{id}", "200")).andExpect(status().isNotFound());
    }

    @Test
    void delete_nonExistingProduct() throws Exception {
        // TODO: Wrong answer, should be 404 not 204
        mvc.perform(delete("/api/products/{id}", "100")).andExpect(status().isNoContent());
    }

    @Test
    void delete_existingProduct() throws Exception {
        Product product = Product.builder()
                .name("nakki")
                .description("nakki teline")
                .price(BigDecimal.valueOf(10.15))
                .build();

        productService.saveProduct(product);
        mvc.perform(delete("/api/products/{id}", 100)).andExpect(status().isNoContent());

    }

}