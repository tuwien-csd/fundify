# FUNDify

## Getting Started

### Development setup


1. **Set up Husky for Git Hooks**
   ```bash
   npx husky init
   npm run prepare
   ```

2. **Install Dependencies**
   ```bash
   npm install
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

#### Code cleanup 

For keeping your code clean, you may run the linter with following command:

```bash
npm run lint
````

For applying the prettier formatter, you may run the following command:

```bash
npx prettier --write .
````