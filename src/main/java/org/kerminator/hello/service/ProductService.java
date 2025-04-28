package org.kerminator.hello.service;

import org.kerminator.hello.model.Product;
import org.kerminator.hello.repository.ProductRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            Product existingProduct = product.get();
            existingProduct.setName(productDetails.getName());
            existingProduct.setDescription(productDetails.getDescription());
            existingProduct.setPrice(productDetails.getPrice());
            existingProduct.setStockQuantity(productDetails.getStockQuantity());
            existingProduct.setInStock(productDetails.getInStock());
            return productRepository.save(existingProduct);
        }
        return null; // Or throw an exception
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Optional<Product> findMostExpensiveProduct() {
        return productRepository.findTopByOrderByPriceDesc();
    }
    
    public List<Product> findProductsByStockAvailability(Boolean inStock) {
        return productRepository.findByInStock(inStock);
    }
    
}
