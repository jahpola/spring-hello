package org.kerminator.hello;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kerminator.hello.controllers.ProductController;
import org.kerminator.hello.model.Product;
import org.kerminator.hello.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(ProductController.class)
class HelloControllerTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    Product product;

    @BeforeEach
    void setup() {
        product = new Product(100L, "nakki", "nakki teline", BigDecimal.valueOf(10.15), 12, false);

        productService.saveProduct(product);
    }

    @Test
    void shouldCreateProduct() throws Exception {
        given(productService.saveProduct(any(Product.class))).willReturn(product);

        mvc.perform(MockMvcRequestBuilders
                .post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(jsonPath("$.name").value("nakki"))
                .andExpect(jsonPath("$.price").value(10.15))
                .andExpect(status().isCreated());
    }

    @Test
   @Disabled("Fix this test")
    void shouldUpdateProduct() throws Exception {
        given(productService.getProductById(product.getId())).willReturn(Optional.of(product));
        product.setDescription("Ei ole kukkateline");
        product.setPrice(BigDecimal.valueOf(99.99));
        given(productService.updateProduct(product.getId(), product)).willReturn(product);

        mvc.perform(MockMvcRequestBuilders
                .put("/api/products/{id}", product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk());
    }

    @Test
    void find_existingProduct() throws Exception {
        given(productService.getProductById(product.getId())).willReturn(Optional.of(product));

        mvc.perform(get("/api/products/{id}", product.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void findAllProducts() throws Exception {
        mvc.perform(get("/api/products")).andExpect(status().isOk());
    }

    @Test
    void find_nonExistingProduct() throws Exception {
        mvc.perform(get("/api/products/{id}", 200)).andExpect(status().isNotFound());
    }

    @Test
    void delete_nonExistingProduct() throws Exception {
        mvc.perform(delete("/api/products/{id}", "100")).andExpect(status().isNoContent());
    }

    @Test
    void delete_existingProduct() throws Exception {
        mvc.perform(delete("/api/products/{id}", 1)).andExpect(status().isNoContent());
    }

}