package at.ac.tuwien.refop.adapters.out.persistence.mongo.annotating;

import at.ac.tuwien.refop.adapters.out.persistence.mongo.common.MongoCrossReferenceResolver;
import at.ac.tuwien.refop.adapters.out.persistence.mongo.common.ObjectIdUtils;
import at.ac.tuwien.refop.application.port.out.persistence.VocabularyRepository;
import at.ac.tuwien.refop.domain.common.Vocabulary;
import at.ac.tuwien.refop.domain.common.VocabularyId;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class VocabularyMongoRepository implements VocabularyRepository {

    private final MongoCrossReferenceResolver mongoCrossReferenceResolver;

    @Override
    public VocabularyId persistVocabulary(Vocabulary vocabulary) {
        VocabularyMongoEntity entity = VocabularyMongoEntityMapper.INSTANCE.toEntity(vocabulary);
        entity.persist();
        return VocabularyMongoEntityMapper.INSTANCE.toDomain(entity, mongoCrossReferenceResolver).id();
    }

    @Override
    public VocabularyId updateVocabulary(Vocabulary vocabulary) {
        VocabularyMongoEntity entity = VocabularyMongoEntityMapper.INSTANCE.toEntity(vocabulary);
        entity.update();
        return VocabularyMongoEntityMapper.INSTANCE.toDomain(entity, mongoCrossReferenceResolver).id();
    }

    @Override
    public Optional<Vocabulary> getById(VocabularyId id) {
        VocabularyMongoEntity entity = VocabularyMongoEntity.findById(ObjectIdUtils.toObjectId(id.value()));
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(VocabularyMongoEntityMapper.INSTANCE.toDomain(entity, mongoCrossReferenceResolver));
    }

}