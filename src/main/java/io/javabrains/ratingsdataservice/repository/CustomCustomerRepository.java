package io.javabrains.ratingsdataservice.repository;

import io.javabrains.ratingsdataservice.model.Customer;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface CustomCustomerRepository {
    public List<Customer>getSome(int from,int to);
    List<Customer> getByComplexId(int add);
}
