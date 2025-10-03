# FUNDify
## Getting Started

### Development setup
### Prerequisites
- **Java 21**
- **Maven**
- **Docker & Docker Compose**: Required for running auxiliary services (MongoDB, Keycloak, Papercut)
- **MongoDB**: Used as the primary database for storing application data
- **Keycloak**: Used for authentication and authorization
- **IDE**: Any Java IDE with Quarkus support (IntelliJ IDEA, Eclipse, VS Code with extensions)
- **Git**: For version control

### Environment variables / Quarkus configuration
- Basic application configuration is done via the [application.yaml](./bootstrap/src/main/resources/application.yaml) file.
- Sensitive values like secrets or API keys are provided via the `.env` file, which is loaded by the `bootstrap` module. Due to Quarkus specifics, this file needs to be in the bootstrap-module folder
- To provide your own values, copy the [.env.example](./bootstrap/.env.example) file to `.env` and adjust the values to your needs.
### Auxiliary services
- FUNDify requires a running MongoDB, Keycloak and Papercut instance.
- Then, run the following command to start the services:
```bash
docker-compose -f compose/docker-compose.development.yaml up -d
```
#### Email Service Testing
- Use Papercut-SMTP service for local email testing
- Access the Papercut web interface at http://localhost:8084
- Email batch settings can be configured in `application.yaml`:
    - `fundify.notification.email.batch-size`
    - `fundify.notification.every.interval.email-poll`

### Building and Running the Application
- To build the application:
  ```bash
  ./mvnw clean package
  ```
- To run the application in development mode with hot reload:
  ```bash
  ./mvnw quarkus:dev
  ```

## Adding a new funding data source
- Update the following sections of the [application.yaml](./bootstrap/src/main/resources/application.yaml)
  - `fundify.external-fundings.data-providers`
  - `quarkus.oidc-client`
  - `quarkus.rest-client`
- The values you need to be set should be clear from the existing entries
- Add entries to the [.env](./bootstrap/.env) and the [.env.example](./bootstrap/.env.example) files
- Add a new interface that extends the [GenericRisFundingRestClient](./adapters/src/main/java/at/ac/tuwien/refop/adapters/out/ris/network/client/GenericRisFundingRestClient.java)
  - You can take a look at the existing [FwfTestRestClient](./adapters/src/main/java/at/ac/tuwien/refop/adapters/out/ris/network/client/FwfTestRestClient.java) for guidance
  - Remember to update the config keys and your implementation of `getMemberId`
- Inject your new interface into the constructor of [RisSynergyNetworkAdapter](./adapters/src/main/java/at/ac/tuwien/refop/adapters/out/ris/network/RisSynergyNetworkAdapter.java) and add it to the map of registered clients

# Technical Debt
- Convoluted data model
  - There are multiple representations of the almost same object in the same layer, e.g. `AnnotatedCallDTO`and `AnnotatedCall`
  - Additionally, there is the concept of `References`, e.g. `FunderReference`
  - This probably was the result of the chosen database technology bleeding into the way domain objects are modeled
  - **Action item: Simplify the data model**
- Naming of DTOs
  - What would normally be called DTOs is referred to as `WebModel` in the controller
  - Some DTOs are defined in the domain layer
  - **Action item: Remove DTOs from domain layer, rename WebModels to DTOs**
- MongoCrossReferenceResolver
  - Despite using MongoDB, the data was modelled resembling a relational database
  - The lack of DB joins was made up by using a custom resolver, which sequentially queries for relations during the mapping
  - While this works for very small collections, this will become very slow once the database grows
  - **Action item: Either migrate to a relational database like PostgreSQL or adjust the data model**
- RESTful paths for all resources
  - Some resources have RESTful paths, while others use verbs like `/add`, `/update`, `/delete`
  - **Action item: Make all resources RESTful**
- Lack of input validation
  - Input validation of incoming POST/PUT requests is mostly missing
  - **Action item: Add input validation for all incoming requests**
- 