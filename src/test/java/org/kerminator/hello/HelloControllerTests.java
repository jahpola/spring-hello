package org.kerminator.hello;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kerminator.hello.controllers.ProductController;
import org.kerminator.hello.model.Product;
import org.kerminator.hello.repository.ProductRepository;
import org.kerminator.hello.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;

@WebMvcTest(ProductController.class)
class HelloControllerTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
    private ProductService productService;


//    @BeforeAll
//    public void init() {
//        Product product = Product.builder()
//                .id(1L)
//                .name("nakki")
//                .description("nakki teline")
//                .price(BigDecimal.valueOf(10.15))
//                .build();
//        //when(productRepository.findById(1L).thenReturn(Optional.of(product));
//    }
    //@Test
//    void shouldCreateProduct() throws Exception {
//
//
//        mvc.perform(post("/products")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(product)))
//                .andExpect(status().isCreated());
//                //.andExpect(jsonPath("$.name").value("Nakki Teline"))
//                //.andExpect(jsonPath("$.price").value(10.15));
//    }

    @Test
    void find_nonExistingProduct() throws Exception {
        mvc.perform(get("/products/100")).andExpect(status().isNotFound());
    }


} 