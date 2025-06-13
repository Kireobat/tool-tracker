# tool-tracker

Backend REST API service for managing tools, built with Spring Boot and Kotlin.

- [tool-tracker](#tool-tracker)
  - [Local Development](#local-development)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Run Tests](#run-tests)
    - [How to use the API](#how-to-use-the-api)
      - [Local development](#local-development-1)
      - [In Production](#in-production)

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

4. Access the application at [http://localhost:8080/tool-tracker/](http://localhost:8080/tool-tracker/).

### Run Tests

To run tests, use the following command:

```sh
mvn test
```

### How to use the API

#### Local development

Almost every endpoint requires authentication, meaning you need to login via the `user/login` endpoint first.

If you dont have a user, you can create one via the `user/register` endpoint, but to give yourself the correct roles you need to edit the table `users_map_roles` in the database.

This sql should give you the correct roles for your user (remember to replace the placeholders):

```sql
INSERT INTO tool_tracker.users_map_roles (user_id, role_id, created_by, created_time) VALUES (:your_user_id, 0, 0, current_timestamp); // Admin role
INSERT INTO tool_tracker.users_map_roles (user_id, role_id, created_by, created_time) VALUES (:your_user_id, 3, 0, current_timestamp); // Employee role
```

After you have updated the roles in the database, you will need to relogin to get the correct roles.

The login is saved in a cookie called `JSESSIONID`, which is used for authentication in subsequent requests.

The only unauthenticated endpoint except for login and register is the `tools` and `tools/{id}` endpoints, they allow unauthenticated users to view the list of `AVAILABELE` tools and a specific `AVAILABELE` tool by ID. When you are authenticated and have the role of `EMPLOYEE`, you can also see `UNAVAILABELE` and `SERVICE` tools.

#### In Production

In production you will have to contact a system admin if you want any roles. For [https://api.kireobat.eu/tool-tracker/](https://api.kireobat.eu/tool-tracker/) that would be me.

### Common problems

If you are using intelliJ and not running `mvn spring-boot:run "-Dspring.profiles.active=local"` (windows) in the cli, but instead pressing the run button you may get this error:

```
Caused by: org.springframework.util.PlaceholderResolutionException: Could not resolve placeholder 'SPRING_DATASOURCE_URL' in value "${SPRING_DATASOURCE_URL}" <-- "${spring.datasource.url}"
```

This is due to you not running the program with the local profile, and therefore it expects some env variables. To fix this in IntelliJ you can click the `Run / Debug Configurations` button, click `More Actions` on `ToolTrackerApplication`, then click `Edit` and in `Active profiles` write `local`
