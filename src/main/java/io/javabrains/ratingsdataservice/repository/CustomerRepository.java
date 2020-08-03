package io.javabrains.ratingsdataservice.repository;

import io.javabrains.ratingsdataservice.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository  extends JpaRepository<Customer,Long> {
    Customer findByName(String alley);

}
