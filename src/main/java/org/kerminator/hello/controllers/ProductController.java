package org.kerminator.hello.controllers;

import org.kerminator.hello.exception.ProductNotFoundException;
import org.kerminator.hello.model.Product;
import org.kerminator.hello.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.observation.annotation.Observed;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Observed(name = "create:Product")
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody @Valid Product product) {
        Product savedProduct = productService.saveProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable(name = "id") Long id) {
        Product product = productService.getProductById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
        return ResponseEntity.ok(product);
    }

     @GetMapping
     public ResponseEntity<Page<Product>> getAllProducts(@PageableDefault(size=20) Pageable pageable) {
        Page<Product> products = productService.getAllProducts(pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
     }
    
    @Observed(name = "update:Product")
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable(name="id") Long id, @RequestBody @Valid Product productDetails) {
        Product updatedProduct = productService.updateProduct(id, productDetails);
        return ResponseEntity.ok(updatedProduct);
    } 

    @Observed(name = "delete:Product")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable(name="id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}