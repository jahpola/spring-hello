package org.kerminator.hello.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@Column(nullable = false)
    private String name;
	
	@Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "in_stock")
    private Boolean inStock;
    
}
