package at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating;

import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.ObjectIdUtils;
import at.ac.tuwien.fundify.application.port.out.persistence.UniversityQuery;
import at.ac.tuwien.fundify.domain.annotating.University;
import at.ac.tuwien.fundify.domain.annotating.UniversityId;
import at.ac.tuwien.fundify.domain.annotating.UniversityReference;
import at.ac.tuwien.fundify.domain.common.RisId;
import at.ac.tuwien.fundify.domain.dto.UniversityIdMapping;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;

@ApplicationScoped
public class UniversityMongoQuery implements UniversityQuery {
    @Override
    public Optional<UniversityIdMapping> findByRisId(RisId risId) {
        UniversityIdentifierProjection entity = UniversityMongoEntity.find("risId", risId.toString())
                .project(UniversityIdentifierProjection.class)
                .firstResult();
        if (entity == null) {
            return Optional.empty();
        }
        return  Optional.of(new UniversityIdMapping(new UniversityId(entity._id().toHexString())));
    }

    @Override
    public Optional<University> findById(UniversityId id) {
        UniversityMongoEntity universityMongoEntity = UniversityMongoEntity.findById(ObjectIdUtils.toObjectId(id.value()));
        if (universityMongoEntity == null) {
            return Optional.empty();
        }
        return Optional.of(UniversityMongoEntityMapper.INSTANCE.toDomain(universityMongoEntity));
    }

    @Override
    public List<University> list() {
        return UniversityMongoEntityMapper.INSTANCE.toDomain(UniversityMongoEntity.listAll());
    }

    @Override
    public Optional<UniversityReference> findReferenceById(UniversityId id) {
        UniversityReferenceProjection entity = UniversityMongoEntity.find("_id", new ObjectId(id.value()))
                .project(UniversityReferenceProjection.class).firstResult();
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(UniversityReferenceProjectionMapper.INSTANCE.toDomain(entity));
    }

    @Override
    public Optional<UniversityReference> findReferenceByEmailDomain(String emailDomain) {
        UniversityReferenceProjection entity = UniversityMongoEntity.find("emailDomain", emailDomain)
                .project(UniversityReferenceProjection.class).firstResult();
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(UniversityReferenceProjectionMapper.INSTANCE.toDomain(entity));
    }
}