package io.javabrains.ratingsdataservice.rest;

import io.javabrains.ratingsdataservice.model.Complex;
import io.javabrains.ratingsdataservice.projections.ComplexProjection;
import io.javabrains.ratingsdataservice.repository.ComplexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
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

    @RequestMapping("/by-title")
    public ResponseEntity<ComplexProjection> getByTitle(@RequestHeader String price){
        return new ResponseEntity<>(complexRepository.findByPrice(new BigInteger(price)), HttpStatus.OK);
    }

    @RequestMapping("/by-name")
    public ResponseEntity<List<Complex>> findFromName(@RequestHeader String title){
        return new ResponseEntity<>(complexRepository.findByTitleLike("%"+title+"%"), HttpStatus.OK);
    }
    @RequestMapping("/id-between/{from}/{to}/{page}/{size}/{isAsc}")
    public ResponseEntity<List<ComplexProjection>> findByIdBetween(@PathVariable Long from,
                                                         @PathVariable Long to,
                                                       int page,int size,boolean isAsc){
        PageRequest pageRequest = new PageRequest(page,size, isAsc?Sort.by("title").ascending():Sort.by("title").descending());
        return new ResponseEntity<>(complexRepository.findByIdBetween(from,to, pageRequest), HttpStatus.OK);
    }



}
