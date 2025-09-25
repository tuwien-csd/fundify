package at.ac.tuwien.refop.adapters.out.persistence.mongo.annotating;

import at.ac.tuwien.refop.adapters.out.persistence.mongo.common.ObjectIdUtils;
import at.ac.tuwien.refop.application.port.out.persistence.UniversityRepository;
import at.ac.tuwien.refop.domain.annotating.University;
import at.ac.tuwien.refop.domain.annotating.UniversityCreate;
import at.ac.tuwien.refop.domain.annotating.UniversityId;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;



@ApplicationScoped
public class UniversityMongoRepository implements UniversityRepository,
    PanacheMongoRepository<UniversityMongoEntity> {

    @Override
    public University persist(University university) {
        UniversityMongoEntity entity = UniversityMongoEntityMapper.INSTANCE.fromDomain(university);
        entity.persist();
        return UniversityMongoEntityMapper.INSTANCE.toDomain(entity);
    }

    @Override
    public University add(UniversityCreate university) {
        UniversityMongoEntity entity = UniversityMongoEntityMapper.INSTANCE.fromDomain(university);
        persist(entity);
        return UniversityMongoEntityMapper.INSTANCE.toDomain(entity);
    }

    @Override
    public Optional<University> update(University university) {
      UniversityMongoEntity entity = UniversityMongoEntityMapper.INSTANCE.fromDomain(university);
      update(entity);
      return Optional.ofNullable(UniversityMongoEntityMapper.INSTANCE.toDomain(entity));
    }

    @Override
    public Optional<University> findById(UniversityId id) {
      UniversityMongoEntity entity = findById(ObjectIdUtils.toObjectId(id.value()));
      return Optional.ofNullable(UniversityMongoEntityMapper.INSTANCE.toDomain(entity));
    }

    @Override
    public List<University> listAllUniversities() {
      return UniversityMongoEntityMapper.INSTANCE.toDomain(listAll());
    }

    @Override
    public boolean delete(UniversityId id) {
        return deleteById(ObjectIdUtils.toObjectId(id.value()));
    }
}