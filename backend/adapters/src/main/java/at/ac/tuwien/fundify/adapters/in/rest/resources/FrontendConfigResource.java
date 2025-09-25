package at.ac.tuwien.fundify.adapters.in.rest.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.Data;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/api/config")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
public class FrontendConfigResource {

    @ConfigProperty(name = "frontend.auth.url")
    String authUrl;

    @ConfigProperty(name = "frontend.auth.client")
    String authClient;

    @ConfigProperty(name = "frontend.auth.scope")
    String authScope;

    @ConfigProperty(name = "frontend.env")
    String env;

    @GET
    public Config config() {
        Config config = new Config();
        config.setAuthUrl(authUrl);
        config.setAuthClient(authClient);
        config.setAuthScope(authScope);
        config.setEnv(env);

        return config;
    }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Config {

      private String authUrl;
      private String authClient;
      private String authScope;
      private String env;
  }
}