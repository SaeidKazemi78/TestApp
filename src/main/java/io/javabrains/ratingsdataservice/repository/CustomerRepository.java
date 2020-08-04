package io.javabrains.ratingsdataservice.repository;

<<<<<<< HEAD
import io.javabrains.ratingsdataservice.dto.CustomDto;
=======
>>>>>>> 6c9f13455c7294105efe0ebb677cc0d710da047e
import io.javabrains.ratingsdataservice.enums.CarType;
import io.javabrains.ratingsdataservice.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository  extends JpaRepository<Customer,Long> {

    Customer findByName(String alley);

<<<<<<< HEAD
    @Query("select new io.javabrains.ratingsdataservice.dto.CustomDto( customer.name,com.title )" +
            "from Customer customer " +
            " left join  Complex  com on  customer.complexId = com.id  ")
    List<CustomDto> getByQuery();

//    @Query("select  cus from Customer  cus where cus.carType=:carType")
//    List<Customer> getNullCustomers(CarType carType);

    List<Customer> getByCarType(CarType carTypel);

//select custom.name,custom.family from Custom costom inner join Compplex complex
=======
//    @Query("select customer from Customer")
//    Object getByQuery(String name, CarType carType);

>>>>>>> 6c9f13455c7294105efe0ebb677cc0d710da047e
}
