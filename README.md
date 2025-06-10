# tool-tracker

Backend REST API service for managing tools, built with Spring Boot and Kotlin.

- [tool-tracker](#tool-tracker)
  - [Local Development](#local-development)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Run Tests](#run-tests)

## Local Development

### Prerequisites

- JDK 21
- Kotlin 1.9.25
- Maven 3.9.9
- Docker

### Installation

1. Clone the repository:

```sh
git clone https://github.com/Kireobat/tool-tracker.git
cd tool-tracker
```

2. Install dependencies:

```sh
mvn clean install
```

3. Run the application:

UNIX
```sh
mvn spring-boot:run -Dspring.profiles.active=local
```

Windows
```sh
mvn spring-boot:run "-Dspring.profiles.active=local"
```

4. Access the application at [http://localhost:8080/tool-tracker/swagger-ui/index.html#/](http://localhost:8080/tool-tracker/swagger-ui/index.html#/).

### Run Tests

To run tests, use the following command:

```sh
mvn test
```
