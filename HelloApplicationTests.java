/* package org.kerminator.hello;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.kerminator.hello.model.Product;
import org.kerminator.hello.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
    private ProductService productService;

/* 	@Test
    void testGetAllProducts() throws Exception {
        Product product1 = new Product(1, "ddd", null, null, null);
        Product product2 = new Product(2,"Product 2", "Description 2");
        when(productService.getAllProducts()).thenReturn(Arrays.asList(product1, product2));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[1].name").value("Product 2"));
    }
 */
/*     @Test
    void testGetProductById() throws Exception {
        Product product = new Product("Test Product", "Test Description");
        product.setId(1L);
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

    } */

/*     @Test
    void testCreateProduct() throws Exception {
        Product product = new Product("New Product", "New Description");
        product.setPrice(new BigDecimal("19.99"));
        when(productService.saveProduct(any(Product.class))).thenReturn(product);

        mvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Product"))
                .andExpect(jsonPath("$.description").value("New Description"))
                .andExpect(jsonPath("$.price").value(19.99));
    } */

/*     @Test
    void testUpdateProduct() throws Exception {
        Product updatedProduct = new Product("Updated Product", "Updated Description");
        updatedProduct.setId(1L);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    void testDeleteProduct() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    } */
} */