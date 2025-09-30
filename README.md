# Fundify
### Project Overview

FUNDify is an integrated funding management platform developed as a core component of the [RIS Synergy](https://forschungsdaten.at/ris) initiative—a strategic project dedicated to digitizing and strengthening the Austrian research landscape.

The platform is designed to establish sustainable, forward-looking data exchange capabilities between funding bodies, educational institutions, and public administration.

The primary goal of this project is to significantly reduce the administrative burden for researchers and staff by fully embracing the **Once-Only Principle**. This ensures that data related to funding applications, projects, and organizations is exchanged efficiently across systems, preventing researchers and institutions from having to submit the same information multiple times.

#### Core Functionality & Users
FUNDify serves as a unified, nationwide interface that centralizes the tracking and internal administration of grant opportunities.

- **Funders**: Utilize the platform to publish official funding opportunities in the form of *calls* or *programs*.
- **Educational Institutions**: Access and annotate the published *calls* or *programs* for their affiliated researchers.

The key functional value lies in the internal annotation feature. This allows institutions to filter, add localized requirements, and administer specific Call data directly to their research staff, ensuring the efficient and targeted distribution of funding information.

### Backend Architecture & Stack

#### Technology Stack
- **Java 21**: The backend is built using Java 21 as indicated in the pom.xml file.
- **Quarkus Framework**: The backend uses Quarkus 3.25.0, a Kubernetes-native Java framework optimized for containerized applications.
- **MongoDB**: Used as the primary database for storing application data.
- **Keycloak**: Integrated for authentication and authorization.
- **Hexagonal Architecture**: The project follows a hexagonal (ports and adapters) architecture pattern, evident from the module structure:
    - `domain`: Core business logic and entities
    - `application`: Use cases and business rules
    - `adapters`: External interfaces (REST, database, etc.)
    - `bootstrap`: Application startup and configuration

#### Requirements
- MongoDB database
- Keycloak for authentication
- Java 21 runtime
- Email server (configurable, with Papercut-SMTP available for local development)

See the [backend README](./backend/README.md) for development setup and further technical details.

### Frontend Architecture & Stack

#### Technology Stack
- **Angular 20**: The frontend is built with Angular 20.1.4, a modern TypeScript-based web framework.
- **Angular Material**: Used for UI components.
- **NgRx**: Implements state management using NgRx (Store, Effects, Entity, Router-Store, Signals).
- **TailwindCSS**: Used for styling.
- **OpenAPI**: Integrates with backend using OpenAPI-generated TypeScript clients.
- **OAuth2/OIDC**: Authentication via angular-oauth2-oidc library.
- **RxJS**: For reactive programming.


#### Requirements
- Node.js 22.18
- Modern web browser
- Connection to the backend API

See the [frontend README](./frontend/README.md) for development setup.

### Technical Debt

This section documents known technical debt in the project to increase visibility and prioritization of improvement efforts.

#### Current Technical Debt Items

| Area | Description                                                                        | Priority | Proposed Solution |
|------|------------------------------------------------------------------------------------|----------|-------------------|
| Frontend | Lack of responsive design, inconsistent implementation through partial refactoring | High     | Complete documentation and implementation guide |
| Backend | MongoDB connection lacks proper error handling and retry logic                     | High     | Implement resilience patterns with circuit breaker |
| Testing | E2E test coverage below 60% for critical user journeys                             | Medium   | Add additional Playwright tests for core workflows |

#### Technical Debt Management Strategy

Our approach to managing technical debt:

1. **Identification**: Regularly review and document technical debt during sprint retrospectives
2. **Prioritization**: Assess impact on development velocity, system stability, and security
3. **Allocation**: Dedicate 20% of each sprint to addressing high-priority technical debt
4. **Prevention**: Code reviews, architecture discussions, and adherence to coding standards
