package org.kerminator.hello.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "product")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstname;
	private String lastname;

	public Product(String firstname, String lastname) {
		this.firstname = firstname;
		this.lastname = lastname;
	}

}
