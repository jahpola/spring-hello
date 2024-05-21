package org.kerminator.hello.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.kerminator.hello.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByLastName(String lastName);
    
    List<Product> findByFirstName(String firstName);

}