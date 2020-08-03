package io.javabrains.ratingsdataservice.repository;

import io.javabrains.ratingsdataservice.enums.CarType;
import io.javabrains.ratingsdataservice.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository  extends JpaRepository<Customer,Long> {

    Customer findByName(String alley);

//    @Query("select customer from Customer")
//    Object getByQuery(String name, CarType carType);

}
