package at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating;

import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.MongoCrossReferenceResolver;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.ObjectIdUtils;
import at.ac.tuwien.fundify.application.port.out.persistence.VocabularyQuery;
import at.ac.tuwien.fundify.domain.annotating.UniversityId;
import at.ac.tuwien.fundify.domain.common.Vocabulary;
import at.ac.tuwien.fundify.domain.common.VocabularyId;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class VocabularyMongoQuery implements VocabularyQuery {

    private final MongoCrossReferenceResolver mongoCrossReferenceResolver;

    @Override
    public Optional<Vocabulary> findById(VocabularyId id) {
        VocabularyMongoEntity entity = VocabularyMongoEntity.findById(ObjectIdUtils.toObjectId(id.value()));
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(VocabularyMongoEntityMapper.INSTANCE.toDomain(entity, mongoCrossReferenceResolver));
    }

    @Override
    public List<Vocabulary> findByUniversityId(UniversityId universityId) {
        List<VocabularyMongoEntity> entities = VocabularyMongoEntity.find("universityId", ObjectIdUtils.toObjectId(universityId.value())).list();
        return entities.stream()
                .map(entity -> VocabularyMongoEntityMapper.INSTANCE.toDomain(entity, mongoCrossReferenceResolver))
                .toList();
    }

    @Override
    public List<Vocabulary> findAll() {
      List<VocabularyMongoEntity> entities = VocabularyMongoEntity.findAll().list();
      return entities.stream()
          .map(entity -> VocabularyMongoEntityMapper.INSTANCE.toDomain(entity, mongoCrossReferenceResolver))
          .toList();
    }

}