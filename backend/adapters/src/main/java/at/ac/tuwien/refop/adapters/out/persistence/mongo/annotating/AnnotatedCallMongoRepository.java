package at.ac.tuwien.refop.adapters.out.persistence.mongo.annotating;

import at.ac.tuwien.refop.adapters.out.persistence.mongo.common.MongoCrossReferenceResolver;
import at.ac.tuwien.refop.adapters.out.persistence.mongo.common.ObjectIdUtils;
import at.ac.tuwien.refop.application.port.out.persistence.AnnotatedCallRepository;
import at.ac.tuwien.refop.domain.annotating.AnnotatedCall;
import at.ac.tuwien.refop.domain.annotating.AnnotatedCallId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class AnnotatedCallMongoRepository implements AnnotatedCallRepository {

    private MongoCrossReferenceResolver mongoCrossReferenceResolver;

    @Inject
    AnnotatedCallMongoRepository(MongoCrossReferenceResolver mongoCrossReferenceResolver) {
        this.mongoCrossReferenceResolver = mongoCrossReferenceResolver;
    }

    @Override
    public AnnotatedCall persist(AnnotatedCall annotatedCall) {
        AnnotatedCallMongoEntity entity = AnnotatedCallMongoEntityMapper.INSTANCE.fromDomain(annotatedCall);
        entity.persist();

        return AnnotatedCallMongoEntityMapper.INSTANCE.toDomain(entity, mongoCrossReferenceResolver);
    }

    @Override
    public AnnotatedCall update(AnnotatedCall annotatedCall) {
        AnnotatedCallMongoEntity entity = AnnotatedCallMongoEntityMapper.INSTANCE.fromDomain(annotatedCall);
        entity.update();
        return AnnotatedCallMongoEntityMapper.INSTANCE.toDomain(entity, mongoCrossReferenceResolver);
    }

    @Override
    public boolean delete(AnnotatedCallId id) {
        return AnnotatedCallMongoEntity.deleteById(ObjectIdUtils.toObjectId(id.value()));
    }

    @Override
    public Optional<AnnotatedCall> findById(AnnotatedCallId id) {
        AnnotatedCallMongoEntity entity = AnnotatedCallMongoEntity.findById(ObjectIdUtils.toObjectId(id.value()));
        return Optional.ofNullable(AnnotatedCallMongoEntityMapper.INSTANCE.toDomain(entity, mongoCrossReferenceResolver));
    }
}