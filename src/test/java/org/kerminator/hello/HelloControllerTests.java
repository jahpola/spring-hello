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

    private Product product;

    @BeforeEach
    public void setUp() {
        product = Product.builder()
                .id(1L)
                .name("nakki")
                .description("nakki teline")
                .price(BigDecimal.valueOf(10.15))
                .build();
    }

    @Test
    void shouldCreateProduct() throws Exception {

        mvc.perform(MockMvcRequestBuilders
                .post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated());
                //.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("nakki"));
                //.andExpect(jsonPath("$.price").value(10.15));
    }

    // @Test
    // void find_existingProduct() throws Exception {
    //     mvc.perform(get("/api/products/1")).andExpect(status().isOk());
    // }

    // @Test
    // void findAllProducts() throws Exception {
    //     mvc.perform(get("/products")).andExpect(status().isOk());
    // }

    @Test
    void find_nonExistingProduct() throws Exception {
        mvc.perform(get("/products/100")).andExpect(status().isNotFound());
    }

    @Test
    void delete_nonExistingProduct() throws Exception {
        mvc.perform(delete("/products/100")).andExpect(status().isNotFound());
    }

}