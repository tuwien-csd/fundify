# FUNDify
## Development setup
### Prerequisites
- TODO
### Environment variables / Quarkus configuration
- Basic application configuration is done via the [application.yaml](./bootstrap/src/main/resources/application.yaml) file.
- Sensitive values like secrets or API keys are provided via the `.env` file, which is loaded by the `bootstrap` module. Due to Quarkus specifics, this file needs to be in the bootstrap-module folder
- To provide your own values, copy the [.env.example](./bootstrap/.env.example) file to `.env` and adjust the values to your needs.
### Auxiliary services
- FUNDify requires a running MongoDB and Keycloak instance.
- You can use the provided [docker-compose.development](./compose/docker-compose.development.yaml) file to start these services.
- First, copy the `.env.template` file to `.env` and adjust the values to your needs
- Then, run the following command to start the services:
```bash
docker-compose -f compose/docker-compose.development.yaml up -d
```
### Running everything in Docker
- TODO: For this, the main docker-compose.yaml can be used, but the keycloak redirect url needs to be adjusted to point to the nginx-proxy

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
