package io.javabrains.ratingsdataservice.rest;

<<<<<<< HEAD
import io.javabrains.ratingsdataservice.enums.CarType;
=======
>>>>>>> 6c9f13455c7294105efe0ebb677cc0d710da047e
import io.javabrains.ratingsdataservice.model.Customer;
import io.javabrains.ratingsdataservice.repository.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.PathVariable;
=======
>>>>>>> 6c9f13455c7294105efe0ebb677cc0d710da047e
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

<<<<<<< HEAD
import java.util.List;

=======
>>>>>>> 6c9f13455c7294105efe0ebb677cc0d710da047e
@RequestMapping("/cu")
@RestController
public class CustomerRest {

    @Autowired
    private CustomerRepository customerRepository;

    @RequestMapping("/cus")
    public ResponseEntity<Customer> getCustommer(@RequestParam("alley") String alley){
        return new ResponseEntity<>(customerRepository.findByName(alley), HttpStatus.OK);
    }

<<<<<<< HEAD
    @RequestMapping("/get-complex")
    public ResponseEntity<Object> getComplexes(@RequestParam("carType") String carType){
        return new ResponseEntity<>(customerRepository.getByQuery(), HttpStatus.OK);
    }

    @RequestMapping("/get-by-car/{car}")
    public ResponseEntity<List<Customer>> getNull(@PathVariable("car") String carType){
        return new ResponseEntity<>(customerRepository.getByCarType(CarType.valueOf(carType)), HttpStatus.OK);
    }


=======
>>>>>>> 6c9f13455c7294105efe0ebb677cc0d710da047e
}
