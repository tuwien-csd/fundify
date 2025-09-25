package at.ac.tuwien.fundify.adapters.out.ris.network.config;

import at.ac.tuwien.fundify.adapters.out.ris.network.client.FwfRestClient;
import at.ac.tuwien.fundify.adapters.out.ris.network.client.FwfTestRestClient;
import at.ac.tuwien.fundify.adapters.out.ris.network.client.GenericRisFundingRestClient;
import at.ac.tuwien.fundify.adapters.out.ris.network.client.WwtfRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@JBossLog
@ApplicationScoped
public class RisClientConfiguration {

  private final Config appConfig = ConfigProvider.getConfig();

  private static final String DATA_PROVIDER_ENABLED_KEY_PATTERN =
      "fundify.external-fundings.data-providers.%s.enabled";

  private static final  boolean DEFAULT_ACTION_IF_NO_KEY_FOUND = false;

  private final Map<String, GenericRisFundingRestClient> registeredRestClients = new HashMap<>();

  RisClientConfiguration(
      @RestClient WwtfRestClient wwtfRestClient,
      @RestClient FwfTestRestClient fwfTestRestClient,
      @RestClient FwfRestClient fwfRestClient) {
    log.info("Initializing RIS client configuration");
    registerIfEnabled(fwfRestClient);
    registerIfEnabled(fwfTestRestClient);
    registerIfEnabled(wwtfRestClient);
    log.infof("Registered RIS clients: %s", registeredRestClients.keySet());
  }

  private void registerIfEnabled(GenericRisFundingRestClient client) {
    String memberId = client.getMemberId();
    if (clientIsEnabled(memberId)) {
      registeredRestClients.put(memberId, client);
    }
  }

  private boolean clientIsEnabled(String memberId) {
    String enabledKey = String.format(DATA_PROVIDER_ENABLED_KEY_PATTERN, memberId);

    var enabledOpt = appConfig.getOptionalValue(enabledKey, Boolean.class);
    if (enabledOpt.isEmpty()) {
      log.infof(
          "Client %s disabled by default, because no matching via configuration key was found for: %s",
          memberId, enabledKey);
      return DEFAULT_ACTION_IF_NO_KEY_FOUND;
    }
    if (enabledOpt.get()) {
      log.infof("Client %s enabled via configuration key %s", memberId, enabledKey);
      return true;
    } else {
      log.infof("Client %s disabled via configuration key %s", memberId, enabledKey);
      return false;
    }
  }

  public Map<String, GenericRisFundingRestClient> getRegisteredRestClients() {
    return Collections.unmodifiableMap(registeredRestClients);
  }
}
