package org.kerminator.hello;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kerminator.hello.model.Product;
import org.kerminator.hello.repository.ProductRepository;
import org.kerminator.hello.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class ProductServiceIntegrationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        productRepository.deleteAll();

        // Create test products
        product1 = new Product();
        product1.setName("Integration Test Product 1");
        product1.setDescription("Integration Test Description 1");
        product1.setPrice(BigDecimal.valueOf(19.99));
        product1.setStockQuantity(10);
        product1.setInStock(true);

        product2 = new Product();
        product2.setName("Integration Test Product 2");
        product2.setDescription("Integration Test Description 2");
        product2.setPrice(BigDecimal.valueOf(29.99));
        product2.setStockQuantity(5);
        product2.setInStock(true);

        // Save products to database
        product1 = productRepository.save(product1);
        product2 = productRepository.save(product2);
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void testSaveProduct() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setDescription("New Description");
        newProduct.setPrice(BigDecimal.valueOf(39.99));
        newProduct.setStockQuantity(15);
        newProduct.setInStock(true);

        // Act
        Product savedProduct = productService.saveProduct(newProduct);

        // Assert
        assertNotNull(savedProduct.getId());
        assertEquals("New Product", savedProduct.getName());
        assertEquals("New Description", savedProduct.getDescription());
        assertEquals(0, BigDecimal.valueOf(39.99).compareTo(savedProduct.getPrice()));
        assertEquals(15, savedProduct.getStockQuantity());
        assertTrue(savedProduct.getInStock());

        // Verify it's in the database
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());
        assertTrue(foundProduct.isPresent());
        assertEquals("New Product", foundProduct.get().getName());
    }

    @Test
    void testGetProductById() {
        // Act
        Optional<Product> foundProduct = productService.getProductById(product1.getId());

        // Assert
        assertTrue(foundProduct.isPresent());
        assertEquals(product1.getId(), foundProduct.get().getId());
        assertEquals(product1.getName(), foundProduct.get().getName());
        assertEquals(product1.getDescription(), foundProduct.get().getDescription());
        assertEquals(0, product1.getPrice().compareTo(foundProduct.get().getPrice()));
    }

    @Test
    void testGetAllProducts() {
        // Act
        List<Product> products = productService.getAllProducts();

        // Assert
        assertEquals(2, products.size());
        assertTrue(products.stream().anyMatch(p -> p.getId().equals(product1.getId())));
        assertTrue(products.stream().anyMatch(p -> p.getId().equals(product2.getId())));
    }

    @Test
    void testUpdateProduct() {
        // Arrange
        Product updatedDetails = new Product();
        updatedDetails.setName("Updated Name");
        updatedDetails.setDescription("Updated Description");
        updatedDetails.setPrice(BigDecimal.valueOf(49.99));
        updatedDetails.setStockQuantity(20);
        updatedDetails.setInStock(false);

        // Act
        Product updatedProduct = productService.updateProduct(product1.getId(), updatedDetails);

        // Assert
        assertNotNull(updatedProduct);
        assertEquals(product1.getId(), updatedProduct.getId());
        assertEquals("Updated Name", updatedProduct.getName());
        assertEquals("Updated Description", updatedProduct.getDescription());
        assertEquals(0, BigDecimal.valueOf(49.99).compareTo(updatedProduct.getPrice()));
        assertEquals(20, updatedProduct.getStockQuantity());
        assertFalse(updatedProduct.getInStock());

        // Verify changes in database
        Optional<Product> foundProduct = productRepository.findById(product1.getId());
        assertTrue(foundProduct.isPresent());
        assertEquals("Updated Name", foundProduct.get().getName());
    }

    @Test
    void testDeleteProduct() {
        // Act
        productService.deleteProduct(product1.getId());

        // Assert
        Optional<Product> deletedProduct = productRepository.findById(product1.getId());
        assertFalse(deletedProduct.isPresent());

        // Verify other products still exist
        Optional<Product> otherProduct = productRepository.findById(product2.getId());
        assertTrue(otherProduct.isPresent());
    }

    @Test
    void testFindMostExpensiveProduct() {
        // Arrange - product2 is already more expensive than product1

        // Act
        Optional<Product> mostExpensive = productService.findMostExpensiveProduct();

        // Assert
        assertTrue(mostExpensive.isPresent());
        assertEquals(product2.getId(), mostExpensive.get().getId());
        assertEquals(0, product2.getPrice().compareTo(mostExpensive.get().getPrice()));
    }

    @Test
    void testUpdateNonExistingProduct() {
        // Arrange
        Long nonExistingId = 9999L;
        Product updatedDetails = new Product();
        updatedDetails.setName("Updated Name");
        updatedDetails.setDescription("Updated Description");
        updatedDetails.setPrice(BigDecimal.valueOf(49.99));

        // Act
        Product result = productService.updateProduct(nonExistingId, updatedDetails);

        // Assert
        assertNull(result);
    }
    
    @Test
    void testFindProductsByStockAvailability() {
        // Arrange - product1 is in stock, product2 is out of stock
        product1.setInStock(true);
        product2.setInStock(false);
        productRepository.save(product1);
        productRepository.save(product2);

        // Act - Find in-stock products
        List<Product> inStockProducts = productService.findProductsByStockAvailability(true);
        
        // Assert
        assertEquals(1, inStockProducts.size());
        assertEquals(product1.getId(), inStockProducts.getFirst().getId());
        
        // Act - Find out-of-stock products
        List<Product> outOfStockProducts = productService.findProductsByStockAvailability(false);
        
        // Assert
        assertEquals(1, outOfStockProducts.size());
        assertEquals(product2.getId(), outOfStockProducts.getFirst().getId());
    }
    
}
