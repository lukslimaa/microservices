package com.vanhack.microservices.restaurantsservice;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantModel, Long> {

	RestaurantModel findByName(String name);

}
