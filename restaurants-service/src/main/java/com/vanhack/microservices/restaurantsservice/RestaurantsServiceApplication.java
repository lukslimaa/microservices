package com.vanhack.microservices.restaurantsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
public class RestaurantsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestaurantsServiceApplication.class, args);
	}
}
