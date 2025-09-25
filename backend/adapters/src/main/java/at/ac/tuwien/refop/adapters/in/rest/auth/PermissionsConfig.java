package at.ac.tuwien.refop.adapters.in.rest.auth;

import at.ac.tuwien.refop.domain.common.UserRole;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;

@ConfigMapping(prefix = "fundify.auth.permissions")
public interface PermissionsConfig {

  Logger LOG = Logger.getLogger(PermissionsConfig.class);

  @WithName("enableDebugLogging")
  Boolean debuggingLoggingEnabled();

  @WithName("mapping")
  List<UserPermissionObject> permissionMapping();

  @WithName("initial-admin")
  Optional<UserPermissionObject> initialAdmin();

  /**
   * Convenience view of permission-mapping as a map keyed by user-id. If duplicate user IDs exist,
   * the last one in the list wins.
   */
  default Map<String, UserPermissionObject> permissionMappingByUserId() {
    Map<String, UserPermissionObject> map = permissionMapping().stream()
        .collect(Collectors.toMap(
            UserPermissionObject::userId,
            Function.identity(),
            (first, second) -> second,
            LinkedHashMap::new
        ));

    if(initialAdmin().isPresent()) {
      map.put(initialAdmin().get().userId(), initialAdmin().get());
    }

    if (debuggingLoggingEnabled()) {
      Set<String> keys = map.keySet();
      LOG.infof("permissionMappingByUserId called: %d users [%s]", keys.size(),
          String.join(", ", keys));
      map.forEach((key, value) -> LOG.infof("  %s -> %s", key, value.stringify()));
    }
    return map;
  }

  interface UserPermissionObject {

    @WithName("userid")
    String userId();

    List<UserRole> roles();

    @WithName("affiliationid")
    String affiliationId();

    default String stringify() {
      return String.format("id: '%s',  roles: '%s', affiliation: '%s'", userId(), roles(),
          affiliationId());
    }
  }
}
