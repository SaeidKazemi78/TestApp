package io.javabrains.ratingsdataservice.repository;

import io.javabrains.ratingsdataservice.model.Complex;
import io.javabrains.ratingsdataservice.projections.ComplexProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


import java.math.BigInteger;
import java.util.List;

@Repository
public interface ComplexRepository  extends JpaRepository<Complex,Long>, PagingAndSortingRepository<Complex,Long> {

//    @Query("select new com.foo.bar.entity.Document(d.docId, d.filename) from Document d where d.filterCol = ?1")

    ComplexProjection findByPrice(BigInteger price);

//    @Query("select customer.name from Customer customer where customer.name like ?1")
    List<Complex> findByTitleLike(String title);


    List<ComplexProjection> findByIdBetween(Long from, Long to,Pageable pageable);
}
