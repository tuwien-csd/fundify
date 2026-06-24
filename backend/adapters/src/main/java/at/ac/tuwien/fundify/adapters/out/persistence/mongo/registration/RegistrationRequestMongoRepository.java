package at.ac.tuwien.fundify.adapters.out.persistence.mongo.registration;

import at.ac.tuwien.fundify.application.port.out.persistence.RegistrationRequestRepository;
import at.ac.tuwien.fundify.domain.registration.RegistrationRequest;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class RegistrationRequestMongoRepository
    implements RegistrationRequestRepository,
        PanacheMongoRepository<RegistrationRequestMongoEntity> {

  @Override
  public RegistrationRequest save(RegistrationRequest request) {
    var entity = RegistrationRequestMongoEntityMapper.INSTANCE.fromDomain(request);
    persistOrUpdate(entity);
    return RegistrationRequestMongoEntityMapper.INSTANCE.toDomain(entity);
  }

  @Override
  public List<RegistrationRequest> findAllRequests() {
    return listAll().stream()
        .map(RegistrationRequestMongoEntityMapper.INSTANCE::toDomain)
        .toList();
  }

  @Override
  public void deleteById(String id) {
    delete("_id", id);
  }
}
