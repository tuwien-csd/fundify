package at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating;

import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.MongoCrossReferenceResolver;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.ObjectIdUtils;
import at.ac.tuwien.fundify.application.port.out.persistence.VocabularyRepository;
import at.ac.tuwien.fundify.domain.common.Vocabulary;
import at.ac.tuwien.fundify.domain.common.VocabularyId;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class VocabularyMongoRepository implements VocabularyRepository {

    private final MongoCrossReferenceResolver mongoCrossReferenceResolver;

    @Override
    public Vocabulary persistVocabulary(Vocabulary vocabulary) {
        VocabularyMongoEntity entity = VocabularyMongoEntityMapper.INSTANCE.toEntity(vocabulary);
        entity.persist();
        return VocabularyMongoEntityMapper.INSTANCE.toDomain(entity, mongoCrossReferenceResolver);
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