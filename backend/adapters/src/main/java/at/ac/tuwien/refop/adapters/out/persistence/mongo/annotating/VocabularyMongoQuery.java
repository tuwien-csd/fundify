package at.ac.tuwien.refop.adapters.out.persistence.mongo.annotating;

import at.ac.tuwien.refop.adapters.out.persistence.mongo.common.MongoCrossReferenceResolver;
import at.ac.tuwien.refop.adapters.out.persistence.mongo.common.ObjectIdUtils;
import at.ac.tuwien.refop.application.port.out.persistence.VocabularyQuery;
import at.ac.tuwien.refop.domain.annotating.UniversityId;
import at.ac.tuwien.refop.domain.common.Vocabulary;
import at.ac.tuwien.refop.domain.common.VocabularyId;
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