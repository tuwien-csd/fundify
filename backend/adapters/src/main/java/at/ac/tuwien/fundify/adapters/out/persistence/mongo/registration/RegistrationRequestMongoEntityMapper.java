package at.ac.tuwien.fundify.adapters.out.persistence.mongo.registration;

import at.ac.tuwien.fundify.domain.registration.RegistrationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RegistrationRequestMongoEntityMapper {
  RegistrationRequestMongoEntityMapper INSTANCE =
      Mappers.getMapper(RegistrationRequestMongoEntityMapper.class);

  RegistrationRequest toDomain(RegistrationRequestMongoEntity entity);

  RegistrationRequestMongoEntity fromDomain(RegistrationRequest request);
}
