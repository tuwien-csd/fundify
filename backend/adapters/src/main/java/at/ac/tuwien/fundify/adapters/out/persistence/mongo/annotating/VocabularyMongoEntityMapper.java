package at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating;

import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.CommonReferenceMapper;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.MongoCrossReferenceResolver;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.MongoEntityIdMapper;
import at.ac.tuwien.fundify.domain.common.Vocabulary;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        MongoEntityIdMapper.class,
        CommonReferenceMapper.class})
public interface VocabularyMongoEntityMapper {

    VocabularyMongoEntityMapper INSTANCE = Mappers.getMapper(VocabularyMongoEntityMapper.class);

    @Mapping(target = "id.value", source = "id")
    @Mapping(target = "university", source = "universityId")
    Vocabulary toDomain(VocabularyMongoEntity source, @Context MongoCrossReferenceResolver resolver);

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "universityId", source = "university.id.value")
    VocabularyMongoEntity toEntity(
        Vocabulary source);

}
