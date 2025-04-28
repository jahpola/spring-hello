package org.kerminator.hello;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kerminator.hello.repository.ProductRepository;
import org.kerminator.hello.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Testcontainers
class HelloRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    private Product product;

    @BeforeEach
    public void setUp() {
        product = new Product();
        product.setName("Makkara");
        product.setDescription("Makkara teline");
        product.setPrice(BigDecimal.valueOf(99.88));

        productRepository.save(product);
        
    }
 
    @AfterEach
    public void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void findById() {
        Optional<Product> foundProduct = productRepository.findById(product.getId());
        assertTrue(foundProduct.isPresent());
    }

    @Test
    void updateProduct() {
        product.setDescription("ihan eri himmeli");
        productRepository.save(product);

        Optional<Product> foundProduct = productRepository.findById(product.getId());
        assertTrue(foundProduct.isPresent());
        assert (foundProduct.get().getDescription().equals("ihan eri himmeli"));
    }

    @Test
    void deleteProductById() {
        productRepository.deleteById(product.getId());

        Optional<Product> foundProduct = productRepository.findById(product.getId());
        assertTrue(foundProduct.isEmpty());
    }
}
