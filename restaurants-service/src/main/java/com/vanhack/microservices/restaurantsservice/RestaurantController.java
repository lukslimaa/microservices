package com.vanhack.microservices.restaurantsservice;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestaurantController {
	
	@Autowired
	private RestaurantRepository repository;

	public RestaurantController() {}
	
	
	@PostMapping("/restaurant")
	public RestaurantModel addNewRestaurant(@Valid @RequestBody RestaurantModel restaurant){
		return repository.save(restaurant);
	}
	
	@GetMapping("/restaurant")
	public List<RestaurantModel> getAllRestaurants() {
		List<RestaurantModel> restaurantsList = repository.findAll();
		restaurantsList.sort(Comparator.comparing(RestaurantModel::getName));
		return restaurantsList;
		
	}
	
	@GetMapping("/restaurant/{id}")
	public RestaurantModel findRestaurantById(@PathVariable(value="id") Long id){
		RestaurantModel restaurant = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", id));
		return restaurant;
	}

}
