# SWYNEX Authentication, Validation and Integration

Task 3 of the SWYNEX Backend Developer Internship. A Spring Boot Student CRUD API extended with:

- **Authentication**: API key (`X-API-KEY` header) protecting all write endpoints
- **Validation**: Bean Validation (`@NotBlank`, `@Email`, `@Size`, `@Min`, `@Max`) on incoming payloads
- **Consistent error handling**: one JSON error format via `@RestControllerAdvice`
- **Documentation and testing**: curl examples, Postman collection, automated tests

## Tech stack
Java 17, Spring Boot 3.2, Spring Web, Spring Data JPA, Bean Validation, H2 (default) / MySQL (optional), Maven.

## Run the project

```bash
# optional: set your own API key (default is change-me-locally)
export API_KEY=mysecret          # Windows PowerShell: $env:API_KEY="mysecret"

mvn spring-boot:run
```

The API starts at `http://localhost:8080`. You can also open the project in IntelliJ / Eclipse / VS Code and run `StudentApplication`.

Run automated tests: `mvn test`

## Endpoints

| Method | URL                  | Auth required | Description        |
|--------|----------------------|---------------|--------------------|
| GET    | `/api/students`      | No            | List all students  |
| GET    | `/api/students/{id}` | No            | Get one student    |
| POST   | `/api/students`      | **Yes**       | Create a student   |
| PUT    | `/api/students/{id}` | **Yes**       | Update a student   |
| DELETE | `/api/students/{id}` | **Yes**       | Delete a student   |

## Authentication

POST, PUT and DELETE require this header:

```
X-API-KEY: <your key>
```

The key is read from the `API_KEY` environment variable (local default: `change-me-locally`).
A missing or wrong key returns **401 Unauthorized**.

## Validation rules

| Field    | Rule                                  |
|----------|---------------------------------------|
| `name`   | required, 2 to 50 characters          |
| `email`  | required, valid email, must be unique |
| `age`    | required, between 5 and 100           |
| `course` | optional, max 50 characters           |

## How to test the protected routes

Replace the key if you set your own `API_KEY`.

**1. No API key -> 401**
```bash
curl -i -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{"name":"Madhuri","email":"madhuri@example.com","age":21,"course":"CS"}'
```

**2. Valid key, invalid payload -> 400**
```bash
curl -i -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" -H "X-API-KEY: change-me-locally" \
  -d '{"name":"","email":"not-an-email","age":2}'
```

**3. Valid key, valid payload -> 201**
```bash
curl -i -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" -H "X-API-KEY: change-me-locally" \
  -d '{"name":"Madhuri","email":"madhuri@example.com","age":21,"course":"CS"}'
```

**4. Public read -> 200**
```bash
curl -i http://localhost:8080/api/students
```

**5. Student not found -> 404**
```bash
curl -i http://localhost:8080/api/students/9999
```

### Postman
Import `postman/SWYNEX-Task3.postman_collection.json`. It contains all the requests above with the `baseUrl` and `apiKey` variables pre-set.

## Error response format

Every error uses the same structure:

```json
{
  "timestamp": "2026-11-07T04:40:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": {
    "name": "Name is required",
    "email": "Email format is invalid",
    "age": "Age must be at least 5"
  }
}
```

| Status | When                                 |
|--------|--------------------------------------|
| 400    | Validation failure or malformed JSON |
| 401    | Missing or invalid API key           |
| 404    | Student not found                    |
| 409    | Duplicate email                      |
| 500    | Unexpected server error              |

## Using MySQL instead of H2
See the commented block at the bottom of `src/main/resources/application.properties`, and replace the `h2` dependency in `pom.xml` with `mysql-connector-j`.

## Project structure

```
src/main/java/com/swynex/student
├── StudentApplication.java
├── controller/StudentController.java
├── model/Student.java
├── repository/StudentRepository.java
├── security/ApiKeyFilter.java
└── exception/
    ├── GlobalExceptionHandler.java
    └── ResourceNotFoundException.java
```

## Security notes
- The real API key is never committed; use the `API_KEY` environment variable (see `.env.example`).
- Key comparison is constant-time.
