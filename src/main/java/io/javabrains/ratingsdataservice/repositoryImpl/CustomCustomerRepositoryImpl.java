package io.javabrains.ratingsdataservice.repositoryImpl;

import com.querydsl.jpa.impl.JPAQuery;
import io.javabrains.ratingsdataservice.model.Customer;
//import io.javabrains.ratingsdataservice.model.QComplex;
//import io.javabrains.ratingsdataservice.model.QCustomer;
import io.javabrains.ratingsdataservice.model.QComplex;
import io.javabrains.ratingsdataservice.model.QCustomer;
import io.javabrains.ratingsdataservice.repository.CustomCustomerRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Objects;

@Repository
public class CustomCustomerRepositoryImpl implements CustomCustomerRepository {


    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Customer> getSome(int from,int to) {
        if(entityManager==null){
            throw new NullPointerException(" Objeeeeeeeeeect nullllll");
        }
        JPAQuery<Customer>query = new JPAQuery<Customer>(entityManager);
        QCustomer qCustomer = new QCustomer("qCus");
        return query.from(qCustomer).where(qCustomer.zipCode.between(from,to)).fetch();
    }

    @Override
    public List<Customer> getByComplexId(int var) {

        JPAQuery<Customer>customerJPAQuery = new JPAQuery<>();
        QCustomer qCustomer = new QCustomer("qCus");
        QComplex qComplex = new QComplex("q");


        if(qCustomer.complexId.id.equals(null)){
            throw new NullPointerException(" NUuuuuuuuuuuuuul ");
        }

       return customerJPAQuery.from(qCustomer).innerJoin(qCustomer.complexId,qComplex).fetch();

    }
}
