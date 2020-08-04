package io.javabrains.ratingsdataservice.projections;

import io.javabrains.ratingsdataservice.enums.CarType;
import io.javabrains.ratingsdataservice.model.Address;
import io.javabrains.ratingsdataservice.model.Complex;

import java.util.Objects;

//This is another types of projection its called class-based projection
//You should have constructor with all arguments
public class CustomerProjection {

    private CarType carType;
    private boolean activated;
    private Complex complexId;

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

    public CustomerProjection(CarType carType, boolean activated, Complex complexId) {
        this.carType = carType;
        this.activated = activated;
        this.complexId = complexId;
    }
}
