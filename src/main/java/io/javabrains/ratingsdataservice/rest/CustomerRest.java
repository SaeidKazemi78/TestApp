package io.javabrains.ratingsdataservice.rest;


import io.javabrains.ratingsdataservice.dto.CustomDto;
import io.javabrains.ratingsdataservice.enums.CarType;

import io.javabrains.ratingsdataservice.model.Customer;
import io.javabrains.ratingsdataservice.projections.CustomerProjection;
import io.javabrains.ratingsdataservice.repository.CustomCustomerRepository;
import io.javabrains.ratingsdataservice.repository.CustomerRepository;

import io.javabrains.ratingsdataservice.repositoryImpl.CustomCustomerRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RequestMapping("/cu")
@RestController
public class CustomerRest {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomCustomerRepositoryImpl customCustomerRepository;

    @RequestMapping("/cus")
    public ResponseEntity<Customer> getCustommer(@RequestParam("alley") String alley){
        return new ResponseEntity<>(customerRepository.findByName(alley), HttpStatus.OK);
    }

    @RequestMapping("/get-complex")
    public ResponseEntity<List<CustomDto> > getComplexes(){
        return new ResponseEntity<>(customerRepository.getByQuery(), HttpStatus.OK);
    }

    @RequestMapping("/get-by-car/{car}")
    public ResponseEntity<List<CustomerProjection>> getNull(@PathVariable("car") String carType,@PathVariable int size,
                                                            @PathVariable int page,@PathVariable boolean isAsc){
        return new ResponseEntity<>(customerRepository.findByCarType(CarType.valueOf(carType), PageRequest.of(page,size,
                isAsc? Sort.by("dasd").ascending():Sort.by("asa").descending() )), HttpStatus.OK);
    }

    @RequestMapping("/get-list-by-dsl")
    public ResponseEntity<List<Customer>> getByDsl(@RequestParam("from") int from,                               @RequestParam("to") int to){
        System.out.println(" Here : from " +from + " to " +to);
        return new ResponseEntity<>(customCustomerRepository.getSome(from,to), HttpStatus.OK);

    }

    @RequestMapping("/get-by-complex-id")
    public ResponseEntity<List<Customer>> getByComplexId(@RequestParam("idLong") int idLong){
        System.out.println(" Here :" +idLong );
        return new ResponseEntity<>(customCustomerRepository.getByComplexId(idLong), HttpStatus.OK);
    }
}
