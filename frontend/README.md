# FUNDify

## Getting Started

### Development setup

1. **Enabling the required node version using [nvm](https://github.com/nvm-sh/nvm)**
    ```bash
   nvm use
   ```

1. **Install Dependencies**
   ```bash
   npm install
   ```

2. **Set up Husky for Git Hooks**
   ```bash
   npm run prepare
   ```


3. **Start the Development Server**
   ```bash
   npm start
   ```
   This will run `ng serve` and make the application available at http://localhost:4200

#### OpenAPI Integration
The frontend uses OpenAPI-generated TypeScript clients to communicate with the backend. You can generate these clients from a running backend instance:
```bash
npm run openapi:generate:from-local-be
```

#### Testing
The frontend includes several testing options:
- Unit tests: `npm test`
- E2E tests: `npm run test:e2e`
- E2E tests with UI: `npm run test:e2e-ui`

For generating E2E tests we use [Playwright](https://playwright.dev/). For that you can run the following command:

```bash
npm run test:e2e-ui:generate`
````
To generate Login credentials before running test generation, run:
```bash
# You might want to adjust the credentials storage to one of (admin|funder|annotator).json
npm run test:e2e-generate-login`
````

#### Linting 

Run the linter with following command:

```bash
npm run lint
````

For applying the prettier formatter, you may run the following command:

```bash
npx prettier --write .
```

# Technical Debt
- State management
  - Most of the state management has been migrated to the new [NgRx Signal Store](https://ngrx.io/guide/signals/signal-store), a lightweight state management library.
  - The old state management is still used in these places; and should be migrated too:
    - Calls
    - Annotations
    - Oefos
  - **Action item: Migrate all state management to NgRx Signal Store and remove the old state management**
- Routing for the calls and annotations module
  - The routing for these models is rather convoluted and not very intuitive
  - **Action item: Refactor the routing for these models**
- Obsolete code
  - The project was migrated from Angular v17 to v20 in a short period of time
  - There are still some leftover `@NgModule`s and other dead code
  - **Action item: Remove all obsolete code**
- E2E tests
  - The E2E tests do not cover all the functionality of the application
  - A setup for running E2E tests in the pipeline is missing
  - **Action item: Add more E2E tests and setup the pipeline to run them**