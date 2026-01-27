package org.kerminator.hello.service;

import org.kerminator.hello.exception.ProductNotFoundException;
import org.kerminator.hello.model.Product;
import org.kerminator.hello.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    /*public List<Product> getAllProducts() {
        return productRepository.findAll();
    }*/

@Transactional
public Product updateProduct(Long id, Product productDetails) {
    Product existingProduct = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
    existingProduct.setName(productDetails.getName());
    existingProduct.setDescription(productDetails.getDescription());
    existingProduct.setPrice(productDetails.getPrice());
    existingProduct.setStockQuantity(productDetails.getStockQuantity());
    existingProduct.setInStock(productDetails.getInStock());
    return productRepository.save(existingProduct);
}

@Transactional
public void deleteProduct(Long id) {
    if (!productRepository.existsById(id)) {
        throw new ProductNotFoundException(id);
    }
    productRepository.deleteById(id);
}

public Optional<Product> findMostExpensiveProduct() {
    return productRepository.findTopByOrderByPriceDesc();
}

public List<Product> findProductsByStockAvailability(Boolean inStock) {
    return productRepository.findByInStock(inStock);
}

}
