# Vanhack-API for Skip the Dishes

This solution show a very simple approaching of how to create microservices and connect one to each other for working together. Ok! But, what is a microservice architecture in fact? What does it stand for?
In a very simple way, microservice is an architectural style which sets up an application as a collection of many small modules of loosely coupled services aiming to implement business capabilities. 

# Technologies and Frameworks

  - [Spring Framework](https://spring.io/)
  - JPA for persistence
  - [Spring Cloud](http://projects.spring.io/spring-cloud/)
  - [Netflix Eureka](https://github.com/Netflix/eureka) for naming servers
  - [Netflix Zuul](https://github.com/Netflix/zuul) for providing an API Gateway
  - [Ribbon](https://github.com/Netflix/ribbon) for loading balance
  - [Swagger](https://swagger.io/) for documenting api.
  - MySQL database.


# Solution Diagram (Services)

You can see below how all the services are disposed and existing connections.

![alt text](https://github.com/lukslimaa/vanhack-api/blob/master/diagram.jpg?raw=true)

# Ribbon Load Balancing

This is how ribbon provide load balancing. Imagine we have on instance of CurrencyCalculationService and multiple instancies of CurrencyExchangeService. How could we make the load balancing when requesting data from CurrencyExchangeService.

![alt text](https://github.com/lukslimaa/vanhack-api/blob/master/ribbon.jpg?raw=true)

### Services

##### Cloud Config Service
***

First thing that we need to mind when we are working with microservices is how to define its configuration. That is, how many instancies of each service will be available to run for each environment (DEV, QA, STAGE, PRODUCTION). For example, the RestaurantService could have this schema, in terms of enviroment, for each environment:

![alt text](https://github.com/lukslimaa/vanhack-api/blob/master/restaurant_service_env_diagram.jpg?raw=true)

All definitions about limits, that is, how many environments each service can have for each macro-environment are made by Config Cloud Service along with Limits Service and they come from a git repository called git-localconfig-repo which is included inside the Cloud Config Service project.

For this solution, I'm considering just two environments (DEV and QA) and the limits are defined in the properties file, such like that:

**limits-service-dev.properties**
```
limits-service.minimum=1
limits-service.maximum=111
```

Alright! Let's run the two services (Cloud Config and Limits) and see how it works in fact.

[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/6n6KK9rotPE/0.jpg)](https://www.youtube.com/watch?v=6n6KK9rotPE)

##### Currency Exchange Service
***

Knowing that Skip the Dishes will be spread across the globe, it's very important to have a service which provides us a currency exchange for calculating the total price of order and give to our customers the opportunity to choose which currency to pay (no excuses! use skip right now!).

This service has two endpoints (really simple idea) which provides:

| Meaning       | Endpoint      |
| ------------- | ------------- |
| Create a new currency exchange | [**POST**] /currency-exchange|
| Get FROM TO currency value | [**GET**] /currency-exchange/from/{from}/to/{to}|

This is a response example for /currency-exchange/from/BRL/to/EUR. I included the port just to make easy to identify later which instance is responding to us.
```json
{
 "id": 2,
 "from": "BRL",
 "to": "EUR",
 "conversionMultiple": 4.45,
 "port": 8000
}
```

Let's see a live example of how this work. For the example below, I'm going to up two instances of Currency Exchange Service.

[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/MPVjYLijgKg/0.jpg)](https://www.youtube.com/watch?v=MPVjYLijgKg)

##### Currency Conversion Service
***

This service is responsible for consulting the currency exchange value based on FROM and TO currencies and then, multiply the got value by the amount desired. Obviously, as I am working with, even simple, microservices architecture, I will use the already known Currency Exchange service to provide my the exchange value. For achieving that, I using Spring Cloud Netflix Tools (Eureka, Ribbon, Feign and Zuul).

Let's see first how does Eureka act for descovering and naming services from my solution. In this video below, I'm going to show you starting Eureka Service and then, restart the other services to show you their appearing in Eureka monitor.

[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/17-uYKODeIM/0.jpg)](https://www.youtube.com/watch?v=17-uYKODeIM)

Now that we have Eureka service running and two instancies of Currency Exchange, we can check Currency Conversion Service connecting to Currency Exchange using our naming server. But, before seeing the video showing the service acting, I just want to point out to a nice approach of how Currency Conversion Service is going to make a request to Currency Exchange Service. For that, I needed to create a proxy interface to make the request to Currency Exchange using Netflix Feign.

**CurrencyExchangeServiceProxy.java**
```java
@FeignClient(name="netflix-zuul-api-gateway-server")
@RibbonClient(name="currency-exchange-service")
public interface CurrencyExchangeServiceProxy {
	
	@GetMapping("/currency-exchange-service/currency-exchange/from/{from}/to/{to}")
	public CurrencyConversionBean retrieveExchangeValue(@PathVariable("from") String from, 
			@PathVariable("to") String to );

}
```

**CurrencyConversionController.java**
```java
@RestController
public class CurrencyConversionController {

	@Autowired
	private CurrencyExchangeServiceProxy proxy;
	
	@GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrency(@PathVariable String from, 
			@PathVariable String to, 
			@PathVariable BigDecimal quantity){
		
		CurrencyConversionBean response = proxy.retrieveExchangeValue(from, to);
		return new CurrencyConversionBean(response.getId(), 
				from, 
				to, 
				response.getConversionMultiple(), 
				quantity, 
				quantity.multiply(response.getConversionMultiple()), 
				response.getPort());
		
	}
	
}
```

Notice that I autowired the proxy interface and then called the method retrieveExchangeValue passing FROM and TO params to retrieve the exchange value.

Let's see a video showing how it works in fact.

[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/IXGEOM7pAUY/0.jpg)](https://www.youtube.com/watch?v=IXGEOM7pAUY)

##### Restaurants Service
***

Finally, I've created a really simple service also using spring boot and JPA for data persistence. In this service I've included swagger for documenting the API. This service aims to add a new restaurant to our database and retrieve all restaurants available or a specific one.

*I was trying to create the services for: restaurants + menu, customers, customers and orders, connecting one to each other, when it makes sense, and showing the final price of order in all possible currencies. However, I didn't have time enough to finish it.*

**Restaurants API Doc created by Swagger**
![alt text](https://github.com/lukslimaa/vanhack-api/blob/master/swagger-api-doc.png?raw=true)

### Plugins

This are all specifications about which port(s) every service runs.

|     Application       |     Port          |
| ------------- | ------------- |
| Limits Service | 8080, 8081, ... |
| Spring Cloud Config Server | 8888 |
| Currency Exchange Service | 8000, 8001, 8002, ..  |
| Currency Conversion Service | 8100, 8101, 8102, ... |
| Restaurants Service | 8200, 8201, ... |
| Netflix Eureka Naming Server | 8761 |
| Netflix Zuul API Gateway Server | 8765 |


# URLs

|     Application       |     URL          |
| ------------- | ------------- |
| Limits Service | http://localhost:8080/limits POST -> http://localhost:8080/actuator/refresh|
|Spring Cloud Config Server| http://localhost:8888/limits-service/default http://localhost:8888/limits-service/dev |
|  Currency Converter Service - Direct Call| http://localhost:8100/currency-converter/from/USD/to/INR/quantity/10|
|  Currency Converter Service - Feign| http://localhost:8100/currency-converter-feign/from/EUR/to/INR/quantity/10000|
| Currency Exchange Service | http://localhost:8000/currency-exchange/from/EUR/to/INR http://localhost:8001/currency-exchange/from/USD/to/INR|
| Eureka | http://localhost:8761/|
| Zuul - Currency Exchange & Exchange Services | http://localhost:8765/currency-exchange-service/currency-exchange/from/EUR/to/INR http://localhost:8765/currency-conversion-service/currency-converter-feign/from/USD/to/INR/quantity/10|

# Final Considerations
As I said, I've got a great idea (I think it is), but it was too much thing to work on for finishing in just few hours. Therefore, as I know that Skip the Dishes is working with microservices architecture, I've decided to build a solution, even it wasn't finished in total, using microservices approach to show that I'm prepared to get at the Skip office and to help my team to build scalable, flexible and reliable solutions. I'm used to work with TDD on my daily basis using jUnit and Mockito, even though I didn't create any test for this solution.
