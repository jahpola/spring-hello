package org.kerminator.hello.repository;

import org.kerminator.hello.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findTopByOrderByPriceDesc();

    List<Product> findByInStock(Boolean inStock);
}
