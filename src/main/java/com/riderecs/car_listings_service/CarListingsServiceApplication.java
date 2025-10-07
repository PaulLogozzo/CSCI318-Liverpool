package com.riderecs.car_listings_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.riderecs.car_listings_service", "com.config"})
@EntityScan("com.riderecs.car_listings_service.entity")
@EnableJpaRepositories("com.riderecs.car_listings_service.repository")
public class CarListingsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarListingsServiceApplication.class, args);
	}

}
