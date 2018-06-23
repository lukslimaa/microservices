# Vanhack-API for Skip the Dishes

This solution show a very simple approaching of how to create microservices and connect one to each other for working together. Ok! But, what is a microservice architecture in fact? What does it stand for?
In a very simple way, microservice is an architectural style which sets up an application as a collection of many small modules of loosely coupled services aiming to implement business capabilities. 


# Technologies and Frameworks

  - Spring Framework
  - JPA for persistence
  - Spring Cloud
  - [Netflix Eureka](https://github.com/Netflix/eureka) for naming servers
  - [Netflix Zuul](https://github.com/Netflix/zuul) for providing an API Gateway
  - [Ribbon](https://github.com/Netflix/ribbon) for loading balance
  - Swagger for documenting api.


# Solution Diagram (Services)

You can see below how all the services are disposed and existing connections.

![alt text](https://github.com/lukslimaa/vanhack-api/blob/master/diagram.jpg?raw=true)

# Ribbon Load Balancing

This is how ribbon provide load balancing. Imagine we have on instance of CurrencyCalculationService and multiple instancies of CurrencyExchangeService. How could we make the load balancing when requesting data from CurrencyExchangeService.

![alt text](https://github.com/lukslimaa/vanhack-api/blob/master/ribbon.jpg?raw=true)
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
