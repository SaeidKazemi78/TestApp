package io.javabrains.ratingsdataservice.model;

import javax.persistence.Embeddable;

@Embeddable
public class Address {


    private String alley;
    private String city;

    public String getAlley() {
        return alley;
    }

    public void setAlley(String alley) {
        this.alley = alley;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
