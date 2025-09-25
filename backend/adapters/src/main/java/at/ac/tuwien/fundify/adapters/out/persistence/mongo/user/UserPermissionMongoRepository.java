package at.ac.tuwien.fundify.adapters.out.persistence.mongo.user;

import at.ac.tuwien.fundify.application.port.out.persistence.UserPermissionRepository;
import at.ac.tuwien.fundify.domain.common.UserPermissionHolder;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class UserPermissionMongoRepository implements UserPermissionRepository, PanacheMongoRepository<UserPermissionMongoEntity> {

  @Override
  public UserPermissionHolder upsert(UserPermissionHolder user) {
    var incoming = UserPermissionMongoEntityMapper.INSTANCE.fromDomain(user);
    persistOrUpdate(incoming);
    return UserPermissionMongoEntityMapper.INSTANCE.toDomain(incoming);
  }

  @Override
  public Optional<UserPermissionHolder> findByUserId(String userId) {
    return find("_id", userId)
        .firstResultOptional()
        .map(UserPermissionMongoEntityMapper.INSTANCE::toDomain);
  }
}
