package io.javabrains.ratingsdataservice.repository;

import io.javabrains.ratingsdataservice.model.Complex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplexRepository  extends JpaRepository<Complex,Long> {


}
