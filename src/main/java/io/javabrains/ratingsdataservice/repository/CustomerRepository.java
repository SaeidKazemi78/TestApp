package io.javabrains.ratingsdataservice.repository;

import io.javabrains.ratingsdataservice.dto.CustomDto;
import io.javabrains.ratingsdataservice.enums.CarType;
import io.javabrains.ratingsdataservice.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository  extends JpaRepository<Customer,Long> {

    Customer findByName(String alley);


//    @Query("select new io.javabrains.ratingsdataservice.dto.CustomDto( customer.name,com.title )" +
//            "from Customer customer " +
//            " left join  Complex  com on  customer.complexId = com.id  ")
//    List<CustomDto> getByQuery();

    @Query("select new io.javabrains.ratingsdataservice.dto.CustomDto( customer.name,com.title )" +
            "from Customer customer " +
            " left join  customer.complexId  com on  customer.complexId = com.id  ")
    List<CustomDto> getByQuery();


//    @Query("select  cus from Customer  cus where cus.carType=:carType")
//    List<Customer> getNullCustomers(CarType carType);

    List<Customer> getByCarType(CarType carTypel);






}
