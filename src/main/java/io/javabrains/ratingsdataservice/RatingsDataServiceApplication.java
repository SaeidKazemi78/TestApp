package io.javabrains.ratingsdataservice;

import io.javabrains.ratingsdataservice.enums.CarType;

import io.javabrains.ratingsdataservice.model.Address;
import io.javabrains.ratingsdataservice.model.Complex;
import io.javabrains.ratingsdataservice.model.Customer;
import io.javabrains.ratingsdataservice.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.math.BigInteger;

@SpringBootApplication
@EnableJpaRepositories
//@EnableEurekaClient
public class RatingsDataServiceApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(RatingsDataServiceApplication.class, args);
	}

	Logger LOGGER = LoggerFactory.getLogger(RatingsDataServiceApplication.class);
	@Autowired
	private CustomerRepository customerRepository;

//	@Autowired
//	private Environment environment;
//
//	@Autowired
//	DataSource dataSource;
//	@Primary
//	@Bean(name = "entityManagerFactory")
//	public EntityManagerFactory entityManagerFactory() {
//		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
//		emf.setDataSource(dataSource);
//		emf.setJpaVendorAdapter(jpaVendorAdapter());
//		emf.setPackagesToScan(new String[]{""});
//		emf.setPersistenceUnitName("default");   // <- giving 'default' as name
//		emf.afterPropertiesSet();
//		return emf.getObject();
//	}
//	@Bean(name = "transactionManager")
//	public PlatformTransactionManager transactionManager() {
//		JpaTransactionManager tm = new JpaTransactionManager();
//		tm.setEntityManagerFactory(entityManagerFactory());
//		return tm;
//	}
//	@Bean
//	public JpaVendorAdapter jpaVendorAdapter() {
//		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
//		jpaVendorAdapter.setShowSql(true);
//		jpaVendorAdapter.setDatabase(Database.H2);
////		jpaVendorAdapter.setDatabasePlatform(environment.getProperty("spring.jpa.database-platform"));
//		jpaVendorAdapter.setGenerateDdl(false);
//		return jpaVendorAdapter;
//	}
	@Override
	public void run(String... args) throws Exception {

		System.out.println( " Hello World !");
		for (int i = 0; i <50 ; i++) {

			Customer customer = new Customer();
			Address address = new Address();
			Complex complex = new Complex();
			complex.setAddress(" Complex Add " +i);
			complex.setPrice(new BigInteger(String.valueOf(25000000) +i));
			complex.setTitle(" Title "+ i);
			customer.setName( "Name " +i);
			customer.setZipCode(i);
			customer.setCarType(CarType.PERSONAL);
			address.setAlley( " Alley " +i);
			address.setCity(" CITY " +i);

			if(i/2==0){
				customer.setComplexId(complex);
			}

			address.setAlley( " Alley " +i);
			address.setCity(" CITY " +i);
			customer.setComplexId(complex);
			customer.setAddress(address);
			customerRepository.save(customer);
			LOGGER.info( " Saved success !");

		}


	}
}

