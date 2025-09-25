package at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.MongoCrossReferenceResolver;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.ObjectIdUtils;
import at.ac.tuwien.fundify.application.port.out.persistence.CallRepository;
import at.ac.tuwien.fundify.domain.common.CallId;
import at.ac.tuwien.fundify.domain.common.RisId;
import at.ac.tuwien.fundify.domain.dto.CallIdMapping;
import at.ac.tuwien.fundify.domain.funding.Call;
import at.ac.tuwien.fundify.domain.funding.CallUpdate;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EIdentifierType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;
import org.bson.Document;

@ApplicationScoped
public class CallMongoRepository implements CallRepository {

    private final MongoCrossReferenceResolver mongoCrossReferenceResolver;

    @Inject
    CallMongoRepository(MongoCrossReferenceResolver mongoCrossReferenceResolver) {
        this.mongoCrossReferenceResolver = mongoCrossReferenceResolver;
    }

    @Override
    public Call persist(Call call) {
        CallMongoEntity entity = CallMongoEntityMapper.INSTANCE.fromDomain(call);
        entity.persist();

        return CallMongoEntityMapper.INSTANCE.toDomain(entity, mongoCrossReferenceResolver);
    }

    @Override
    public Optional<Call> update(Call call) {
        CallMongoEntity entity = CallMongoEntityMapper.INSTANCE.fromDomain(call);
        entity.update();

        return Optional.ofNullable(CallMongoEntityMapper.INSTANCE.toDomain(entity, mongoCrossReferenceResolver));
    }

    @Override
    public Optional<Call> update(CallUpdate callUpdate) {
      CallMongoEntity entity = CallMongoEntityMapper.INSTANCE.fromDomain(callUpdate);
      entity.update();

      return Optional.ofNullable(CallMongoEntityMapper.INSTANCE.toDomain(entity, mongoCrossReferenceResolver));
    }

  @Override
    public boolean delete(CallId id) {
        return CallMongoEntity.deleteById(ObjectIdUtils.toObjectId(id.value()));
    }

    @Override
    public Optional<Call> findById(CallId id) {
        CallMongoEntity call = CallMongoEntity.findById(ObjectIdUtils.toObjectId(id.value()));
        if (call == null) {
            return Optional.empty();
        }
        return Optional.of(CallMongoEntityMapper.INSTANCE.toDomain(call, mongoCrossReferenceResolver));
    }

    @Override
    public Optional<Call> findByRisId(RisId risId) {
        CallMongoEntity call = CallMongoEntity.find("risId", risId.toString()).firstResult();
        if (call == null) {
            return Optional.empty();
        }
        return Optional.of(CallMongoEntityMapper.INSTANCE.toDomain(call, mongoCrossReferenceResolver));
    }

    @Override
    public Optional<CallIdMapping> findIdMappingByRisId(RisId risId) {
        Document query = new Document(
                "identifiers",
                new Document(
                        "$elemMatch",
                        new Document("type", EIdentifierType.RIS_SYNERGY).append("value", risId.toString())
                )
        );

        CallIdentifierProjection call = CallMongoEntity.find(query)
                .project(CallIdentifierProjection.class)
                .firstResult();

        if (call == null) {
            return Optional.empty();
        }

        return Optional.of(new CallIdMapping(new CallId(call._id().toHexString())));
    }
}