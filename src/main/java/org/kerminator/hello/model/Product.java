package org.kerminator.hello.model;

import jakarta.persistence.*;

@Entity
@Table(name = "product")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@Column(name = "firstname")
    private String firstname;

	@Column(name = "lastname")
	private String lastname;

    public Product() {}

	public Product(String firstname, String lastname) {
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public Long getId() {
		return id;
	}

	public String getFirstname() {
		return firstname;
	}

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

	public String getLastname() {
		return lastname;
	}

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    
	@Override
	public String toString() {
		return String.format(
				"Product[id=%d, firstname='%s', lastname='%s']",
				id, firstname, lastname);
	}

}
