package io.javabrains.ratingsdataservice.model;


import io.javabrains.ratingsdataservice.enums.CarType;

import javax.persistence.*;


@Entity
public class Customer {

    @Id@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String zipCode;
    @Enumerated(EnumType.STRING)
    private CarType carType;
    private  boolean activated;

    @Embedded
    private Address address;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="complex_id")
    private Complex complexId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }



    public CarType getCarType() {
        return carType;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public Complex getComplexId() {
        return complexId;
    }

    public void setComplexId(Complex complexId) {
        this.complexId = complexId;
    }
}