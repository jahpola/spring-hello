package org.kerminator.hello;

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

    @Test
    void saveProductAndFindProduct() {
        Product testproduct = Product.builder()
                .name("takki")
                .description("takki teline")
                .price(BigDecimal.valueOf(10.18))
                .build();

        productRepository.save(testproduct);

        Optional<Product> foundProduct = productRepository.findById(testproduct.getId());
        assertTrue(foundProduct.isPresent());
    }
}
