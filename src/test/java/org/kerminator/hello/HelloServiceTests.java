package org.kerminator.hello;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kerminator.hello.model.Product;
import org.kerminator.hello.repository.ProductRepository;
import org.kerminator.hello.service.ProductService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HelloServiceTests {
    
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductService productService;
    
    private Product product1;
    private Product product2;
    
    @BeforeEach
    void setUp() {
        // Initialize test products
        product1 = new Product(1L, "Test Product 1", "Description 1", BigDecimal.valueOf(19.99), 10, true);
        product2 = new Product(2L, "Test Product 2", "Description 2", BigDecimal.valueOf(29.99), 5, true);
    }
    
    @Test
    void testSaveProduct() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(product1);
        
        // Act
        Product savedProduct = productService.saveProduct(product1);
        
        // Assert
        assertNotNull(savedProduct);
        assertEquals(product1.getName(), savedProduct.getName());
        assertEquals(product1.getPrice(), savedProduct.getPrice());
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    void testGetProductById_ExistingProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        
        // Act
        Optional<Product> foundProduct = productService.getProductById(1L);
        
        // Assert
        assertTrue(foundProduct.isPresent());
        assertEquals(product1.getId(), foundProduct.get().getId());
        assertEquals(product1.getName(), foundProduct.get().getName());
        verify(productRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetProductById_NonExistingProduct() {
        // Arrange
        when(productRepository.findById(3L)).thenReturn(Optional.empty());
        
        // Act
        Optional<Product> foundProduct = productService.getProductById(3L);
        
        // Assert
        assertFalse(foundProduct.isPresent());
        verify(productRepository, times(1)).findById(3L);
    }
    
    @Test
    void testGetAllProducts() {
        // Arrange
        List<Product> productList = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(productList);
        
        // Act
        List<Product> foundProducts = productService.getAllProducts();
        
        // Assert
        assertNotNull(foundProducts);
        assertEquals(2, foundProducts.size());
        assertEquals(product1.getName(), foundProducts.get(0).getName());
        assertEquals(product2.getName(), foundProducts.get(1).getName());
        verify(productRepository, times(1)).findAll();
    }
    
    @Test
    void testUpdateProduct_ExistingProduct() {
        // Arrange
        Product updatedProduct = new Product(1L, "Updated Product", "Updated Description", BigDecimal.valueOf(24.99), 15, false);
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        
        // Act
        Product result = productService.updateProduct(1L, updatedProduct);
        
        // Assert
        assertNotNull(result);
        assertEquals(updatedProduct.getName(), result.getName());
        assertEquals(updatedProduct.getDescription(), result.getDescription());
        assertEquals(updatedProduct.getPrice(), result.getPrice());
        assertEquals(updatedProduct.getStockQuantity(), result.getStockQuantity());
        assertEquals(updatedProduct.getInStock(), result.getInStock());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    void testUpdateProduct_NonExistingProduct() {
        // Arrange
        Product updatedProduct = new Product(3L, "Updated Product", "Updated Description", BigDecimal.valueOf(24.99), 15, true);
        
        when(productRepository.findById(3L)).thenReturn(Optional.empty());
        
        // Act
        Product result = productService.updateProduct(3L, updatedProduct);
        
        // Assert
        assertNull(result);
        verify(productRepository, times(1)).findById(3L);
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    void testDeleteProduct() {
        // Arrange
        doNothing().when(productRepository).deleteById(1L);
        
        // Act
        productService.deleteProduct(1L);
        
        // Assert
        verify(productRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testFindMostExpensiveProduct_ExistingProducts() {
        // Arrange
        when(productRepository.findTopByOrderByPriceDesc()).thenReturn(Optional.of(product2)); // product2 has higher price
        
        // Act
        Optional<Product> mostExpensiveProduct = productService.findMostExpensiveProduct();
        
        // Assert
        assertTrue(mostExpensiveProduct.isPresent());
        assertEquals(product2.getId(), mostExpensiveProduct.get().getId());
        assertEquals(product2.getPrice(), mostExpensiveProduct.get().getPrice());
        verify(productRepository, times(1)).findTopByOrderByPriceDesc();
    }
    
    @Test
    void testFindMostExpensiveProduct_NoProducts() {
        // Arrange
        when(productRepository.findTopByOrderByPriceDesc()).thenReturn(Optional.empty());
        
        // Act
        Optional<Product> mostExpensiveProduct = productService.findMostExpensiveProduct();
        
        // Assert
        assertFalse(mostExpensiveProduct.isPresent());
        verify(productRepository, times(1)).findTopByOrderByPriceDesc();
    }
    
    @Test
    void testFindProductsByStockAvailability_InStock() {
        // Arrange
        List<Product> inStockProducts = Collections.singletonList(product1);
        when(productRepository.findByInStock(true)).thenReturn(inStockProducts);
        
        // Act
        List<Product> result = productService.findProductsByStockAvailability(true);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(product1.getId(), result.getFirst().getId());
        verify(productRepository, times(1)).findByInStock(true);
    }
    
    @Test
    void testFindProductsByStockAvailability_OutOfStock() {
        // Arrange
        List<Product> outOfStockProducts = Collections.singletonList(product2);
        when(productRepository.findByInStock(false)).thenReturn(outOfStockProducts);
        
        // Act
        List<Product> result = productService.findProductsByStockAvailability(false);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(product2.getId(), result.getFirst().getId());
        verify(productRepository, times(1)).findByInStock(false);
    }
    
}






