package org.kerminator.hello.service;

import java.util.List;
import org.kerminator.hello.model.*;
import org.kerminator.hello.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return (List<Product>) productRepository.findAll();
    }
    //List<Product> getAllProducts;
}
