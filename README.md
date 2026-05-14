# Employee Leave Management System

A comprehensive Employee Leave Management System built using Java and Spring Boot. Features include automated leave request workflows, role-based access control (RBAC), and persistent data management with Hibernate/JPA.

## Features

- Employee leave request submission & tracking
- Automated leave approval workflow
- Role-based access control (admin, manager, employee)
- Persistent data storage using Hibernate/JPA
- RESTful web services (Spring MVC)
- Easily extensible service and model structure

## Project Structure

```
java-spring-boot-leave-management-system/
├── pom.xml                         # Maven build file with all dependencies
├── schema.sql                      # Database schema definition (for initial setup)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/leavemanagement/
│   │   │        ├── config/        # Spring security and application config
│   │   │        ├── controller/    # REST controllers (API endpoints)
│   │   │        ├── dao/           # Data Access Objects (interfaces and impls)
│   │   │        ├── model/         # Entity classes (Employee, Leave, etc.)
│   │   │        └── service/       # Business logic and service layer
│   │   └── webapp/
│   │        └── WEB-INF/           # Web resources, JSPs, deployment descriptors
│   └── test/
│        ├── java/                  # Unit & integration tests
│        └── resources/             # Test resources
├── .gitignore
```

## Setup & Running

1. **Clone the repo**
    ```bash
    git clone https://github.com/SolankiRavi-hub/java-spring-boot-leave-management-system.git
    cd java-spring-boot-leave-management-system
    ```

2. **Database Setup**
    - Use `schema.sql` to initialize the database.
    - Update your datasource settings in `application.properties` or relevant Spring config.

3. **Build the project**
    ```bash
    mvn clean install
    ```

4. **Run the application**
    ```bash
    mvn spring-boot:run
    ```
    The application will start on `http://localhost:8080` by default.

5. **API Endpoints**
    - Main REST endpoints can be explored in the `controller` package under `com/leavemanagement/controller/`
    - Example endpoints:
        - `POST /api/leave/request` – submit a leave request
        - `GET /api/leave/status` – view status of existing requests

## Key Components

- **Config** ([src/main/java/com/leavemanagement/config/](src/main/java/com/leavemanagement/config/)): Spring-related config such as security, CORS, beans.
- **Controller** ([src/main/java/com/leavemanagement/controller/](src/main/java/com/leavemanagement/controller/)): Handles HTTP requests and maps them to services.
- **DAO** ([src/main/java/com/leavemanagement/dao/](src/main/java/com/leavemanagement/dao/)): Interfaces and implementations for data access.
- **Model** ([src/main/java/com/leavemanagement/model/](src/main/java/com/leavemanagement/model/)): Entity representations of core domain objects (Employee, LeaveRequest, Role, etc.)
- **Service** ([src/main/java/com/leavemanagement/service/](src/main/java/com/leavemanagement/service/)): Business logic for leave processing, approvals, notifications, etc.

## Testing

- Unit and integration tests are located in `src/test/java/`

## Customization

- Update entities or add fields inside the `model/` directory.
- Add new business rules in the `service/` layer.
- Extend endpoints in the `controller/` layer.
- Edit UI/web resources in `src/main/webapp/WEB-INF/`

## Contributing

Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.

---

Made with :coffee: using Spring Boot.
