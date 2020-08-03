package io.javabrains.ratingsdataservice.rest;

import io.javabrains.ratingsdataservice.model.Complex;
import io.javabrains.ratingsdataservice.repository.ComplexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/complex")
public class ComplexRest {


    @Autowired
    private ComplexRepository complexRepository;

    @RequestMapping("/get")
    public ResponseEntity<List<Complex>> getComplexes(){
        return new ResponseEntity<>(complexRepository.findAll(), HttpStatus.OK);
    }
    @RequestMapping("/get/{id}")
    public ResponseEntity<Complex> getOne(@PathVariable Long id){
        return new ResponseEntity<>(complexRepository.findById(id).get(), HttpStatus.OK);
    }
}
