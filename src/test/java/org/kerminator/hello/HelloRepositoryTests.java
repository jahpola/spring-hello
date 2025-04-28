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

    private Product testproduct;

    // @BeforeEach
    // public void setUp() {

        
    // }

    @AfterEach
    public void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void findById() {
        Product product = new Product(100L, "nakki", "nakki teline", BigDecimal.valueOf(10.15), 12, false);
        productRepository.save(product);

        Optional<Product> foundProduct = productRepository.findById(product.getId());
        assertTrue(foundProduct.isPresent());
    }

    @Test
    void updateProduct() {
        testproduct.setDescription("ihan eri himmeli");
        productRepository.save(testproduct);

        Optional<Product> foundProduct = productRepository.findById(testproduct.getId());
        assertTrue(foundProduct.isPresent());
        assert (foundProduct.get().getDescription().equals("ihan eri himmeli"));
    }

    @Test
    void deleteProductById() {
        productRepository.deleteById(testproduct.getId());

        Optional<Product> foundProduct = productRepository.findById(testproduct.getId());
        assertTrue(foundProduct.isEmpty());
    }
}
