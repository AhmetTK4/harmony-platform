#  Harmony Platform

**Harmony Platform** is a modular, event-driven microservices architecture built with **Java 17** and **Spring Boot 3.2**.  
It simulates a basic e-commerce ecosystem using **RabbitMQ** messaging and service orchestration.

---

##  Features

-  Microservices-based architecture  
-  Order, Payment, and Shipping flows with saga-like event coordination  
-  Eureka Service Discovery  
-  RabbitMQ for asynchronous messaging  
-  API Gateway with dynamic routing  
-  Docker & Docker Compose support  
-  Prometheus monitoring integration  

---

##  Microservices

| Service             | Description                             |
|---------------------|-----------------------------------------|
| `gateway-service`   | API Gateway using Spring Cloud Gateway  |
| `user-service`      | Manages user information and identities |
| `product-service`   | Handles product listing and inventory   |
| `order-service`     | Creates and manages customer orders     |
| `payment-service`   | Simulates payment transaction flow      |
| `shipping-service`  | Handles shipping operations             |
| `discovery-service` | Eureka service registry for discovery   |

---

##  Tech Stack

- **Java 17**, **Spring Boot 3.2**
- **RabbitMQ** (Event-driven messaging)
- **Eureka** (Service Discovery)
- **Docker**, **Docker Compose**
- **Gradle** (Build tool)
- **Prometheus** (Monitoring - optional)
- **PostgreSQL** or in-memory DB (can be extended)

---

##  Running Locally

###  Prerequisites

- Java 17+
- Docker & Docker Compose

###  Run with Docker Compose

```bash
docker-compose up --build
```

---

###  Services will be available at:

-  Gateway: http://localhost:8080  
-  Eureka Dashboard: http://localhost:8761  
-  RabbitMQ UI: http://localhost:15672 (guest / guest)  

---

##  Sample Flow

1. A user places an order via `order-service`.  
2. `order-service` publishes an `OrderCreatedEvent` to RabbitMQ.  
3. `payment-service` consumes the event and processes the payment.  
4. If payment succeeds, it publishes a `ShippingCreatedEvent`.  
5. `shipping-service` receives the event and completes the delivery.  

Each service acts **independently** and communicates via **events**.

---

## Build

### To build the whole platform:

```bash
./gradlew clean build
```

### To build a specific service:

```bash
cd order-service
./gradlew build
```

---

## Future Enhancements

- API documentation with Swagger/OpenAPI  
- Kafka support  
- Centralized logging (ELK/EFK)  
- Circuit Breakers (Resilience4j)  
- Distributed Tracing (Sleuth + Zipkin)  

---

## Author

**Ahmet Temel Kundupoğlu**  
*Java Backend Developer & Open Source Enthusiast*  
[**GitHub: AhmetTK4**](https://github.com/AhmetTK4)

## License

This project is licensed under the [MIT License](LICENSE).
