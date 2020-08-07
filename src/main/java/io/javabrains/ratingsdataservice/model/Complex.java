package io.javabrains.ratingsdataservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_complex")
public class Complex {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String complexNum;

    @Lob
    private String address;

    private BigInteger price;


    @JsonIgnore
    @OneToMany(mappedBy = "complexId")
    private Set<Customer> customers = new HashSet<>();

    public Long getId() {
        return id;
    }

//    public Set<Customer> getCustomers() {
//        return customers;
//    }

    public void setCustomers(Set<Customer> customers) {
        this.customers = customers;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigInteger getPrice() {
        return price;
    }

    public void setPrice(BigInteger price) {
        this.price = price;
    }
}
