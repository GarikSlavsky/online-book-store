# Online Book Store

## Introduction

Welcome to Shopping Cart Management System, a web application designed to simplify online shopping by offering powerful cart management features. This project demonstrates my expertise in backend development with Spring Boot, Spring Security, and database technologies, focusing on scalability and secure user interactions.

## Features

- Add and Remove Items: Users can effortlessly add or remove items from their shopping cart.

- Secure Authentication: Role-based access control with Spring Security to ensure data privacy and secure user operations.

- API Documentation: Interactive Swagger UI for seamless API exploration and testing.

- Persistence: Implements Spring Data JPA with MySQL for reliable and efficient data storage.

- Exception Handling: Graceful handling of invalid operations and errors with informative responses.

- Postman Collection: Preconfigured API requests for easier manual testing and exploration.

## Technologies Used

**The application employs the following technologies and tools:**

- Spring Boot (3.4.1): Framework for developing the backend application.

- Spring Security (6.4.2): Provides authentication and role-based access control (part of Spring Boot Starter Security).

- Spring Data JPA (3.4.1): Manages database operations (included in Spring Boot Starter Data JPA).

- Swagger/OpenAPI (2.8.4): Enables REST API documentation and interactive testing.

- JUnit (5.8.2): Supports unit testing and integration testing.

- Mockito (5.14.2): Mocking framework (compatible with JUnit).

- MapStruct (1.6.3): Generates mapping code for DTOs and entities.

- Lombok (1.18.36): Simplifies code by auto-generating boilerplate code such as getters and setters.

- Docker (v28.0.1): Containerization platform used for deploying and managing the application in isolated environments.

- Spring Data JPA: Facilitates database operations with  MySQL.

- Liquibase (4.29.2): Handles database migrations and schema management (includes plugins for Maven).

- MySQL Connector (8.2.0): Establishes connections between the Java application and the MySQL database.

- Jacoco (0.8.12): Monitors and reports test coverage metrics.

- Postman: Provides tools to test and explore API functionalities manually.

## Architecture Overview

**This application follows a modular design to promote scalability and maintainability:**

- Controllers: Handle HTTP requests and delegate business logic.

- Services: Business logic and integration with the repository layer.

- Repositories: Interact with the database using Spring Data JPA.

- Entities: Represent the data structure stored in the database.

- Database Schema:

The schema includes tables for `users`, `roles`, `shopping_carts`, `items`, and `users_roles`. Each table ensures efficient management of user actions, item storage, and shopping cart operations.

## Visual Diagrams

### 1. Model Diagram
Below is the **model diagram**, which illustrates the structure of entities and their relationships in the application:

![Model Diagram](docs/model-diagram.JPG)

---

### 2. Database Diagram
Below is the **database diagram**, showing the table structures, keys, and relationships:

![Database Diagram](docs/database-diagram.JPG)

---

## How to Run the Project

1. Prerequisites: 

- Install Java (version 17 or higher). 

- Install Maven (version 3.6 or higher).

- Set up MySQL and create a database named `shopping_cart`.

2. Steps:
- Clone the repository:

`git clone` [repository](https://github.com/GarikSlavsky/online-book-store)

- Navigate to the project directory:

`cd online-book-store`

- Configure the database in the application.properties file:

```properties

        spring.datasource.url=jdbc:mysql://localhost:3306/shopping_cart

        spring.datasource.username=root

        spring.datasource.password=your_password
```

3. Build and run the application:

```
        mvn clean install

        mvn spring-boot:run
```
4. Access the application:

- Swagger UI: Navigate to [swagger](http://localhost:8088/swagger-ui/index.html#/) to explore the APIs.

- API Endpoints: Use Postman or cURL to interact with endpoints, e.g., `/cart`.

## Challenges & Learnings

During the development of this project, I faced challenges such as:

1. Managing Recursive Relationships: Implementing the recursive structure for `shopping_carts` and `items` while maintaining database integrity.

- ***Solution***: Used proper join mappings and database constraints with JPA annotations.

2. Error Handling: Ensuring robust exception handling for invalid requests.

- ***Solution***: Leveraged `@ControllerAdvice` to return user-friendly error messages.

3. Security Configuration: Configuring Spring Security for role-based access and protecting sensitive endpoints.

- ***Solution***: Applied `@PreAuthorize` annotations and configured security filters.

These challenges enhanced my understanding of backend development and problem-solving in real-world scenarios.

## Demo Video

[Watch the Loom demo](https://www.loom.com/share/241e906d6f1b4eb08208758298a981ae?sid=06c4539e-6368-4bef-8ca7-76f84857591a) to see the application in action.

## Contributing

Contributions are welcome! Feel free to fork this repository, submit issues, or create pull requests to enhance this project further.
