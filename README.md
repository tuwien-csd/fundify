# FUNDify
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
- **Angular 20**: The frontend is built with Angular 20, a modern TypeScript-based web framework.
- **Angular Material**: Used for UI components.
- **NgRx**: Implements state management using NgRx (Store, Effects, Entity, Router-Store, Signals).
- **TailwindCSS**: Used for styling.
- **OpenAPI**: Integrates with backend using OpenAPI-generated TypeScript clients.

#### Requirements
See the [frontend README](./frontend/README.md) for development setup.

### Technical Debt
This project has been developed by various contributors with different experience levels over the course of several years. During the development effort, some technical debt has accrued. Given the limited resources, it is not possible to address all technical debt items at once. The following section documents the current technical debt items.
- Backend: [backend README](./backend/README.md#technical-debt)
- Frontend: [frontend README](./frontend/README.md#technical-debt)
