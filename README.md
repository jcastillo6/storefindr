# StoreFindr

StoreFindr is a Spring Boot application that allows users to find the 5 closest stores based on a given latitude and longitude. The application loads store data from a JSON file and exposes a REST API for querying nearby stores.

## Approach

- **Domain-Driven Design**: The application separates domain logic (store, address, location) from infrastructure and web layers.
- **In-Memory Repository**: Store data is loaded from `stores.json` into an in-memory repository at startup for fast access.
- **Distance Calculation**: The Haversine formula is used to accurately calculate the distance between the user's coordinates and each store.
- **Sorting and Limiting**: Stores are sorted by distance, and only the 5 closest are returned.
- **Validation and Error Handling**: Input coordinates are validated. Invalid requests return a structured error response with appropriate HTTP status codes.
- **OpenAPI/Swagger**: The API is documented using OpenAPI, and the contract is used to generate models and interfaces.
- **Testing**: The application includes unit and BDD tests (Cucumber) to ensure correctness and expected behavior.

## How to Run

### Prerequisites

- Java 21 or higher
- Gradle (the project includes the Gradle Wrapper)

### Running the Application

1. **Clone the repository**
   ```sh
   git clone <your-repo-url>
   cd <your-project-directory>

2. **Start the application**

   ```sh
   ./gradlew bootRun

The application will start on 8080.


### API Usage


#### Find Nearby Stores


GET /stores/nearby?latitude={latitude}&longitude={longitude}
Example:

curl "http://localhost:8080/stores/nearby?latitude=51.778&longitude=4.615"

### Error Handling
Invalid coordinates (e.g., latitude > 90) will return:

```json
{
  "code": "INVALID_COORDINATES",
  "message": "Latitude must be between -90 and 90 degrees"
}
```

### Running Tests


Unit and Integration Tests

```sh
./gradlew test
```

BDD Tests (Cucumber)
Cucumber feature files are located in src/test/resources/features.

### Project Structure
src/main/java/com/jcastillo/storefindr/adapter/in/web - REST controllers and exception handlers
src/main/java/com/jcastillo/storefindr/adapter/out/persistence - In-memory repository
src/main/resources/stores.json - Store data
src/test/java/com/jcastillo/storefindr/bdd - BDD step definitions and configuration

### Contact
For questions or contributions, contact Jorge Castillo at castillo.guerra@gmail.com.