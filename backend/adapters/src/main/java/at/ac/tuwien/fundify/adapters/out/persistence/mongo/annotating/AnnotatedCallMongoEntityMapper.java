package at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating;

import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.CommonReferenceMapper;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.MongoCrossReferenceResolver;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.MongoEntityIdMapper;
import at.ac.tuwien.fundify.domain.annotating.AnnotatedCall;
import at.ac.tuwien.fundify.domain.dto.AnnotatedCallDTO;
import at.ac.tuwien.fundify.domain.dto.CallDTO;
import org.bson.types.ObjectId;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {
        MongoEntityIdMapper.class,
        CommonReferenceMapper.class
})
public interface AnnotatedCallMongoEntityMapper {

    AnnotatedCallMongoEntityMapper INSTANCE = Mappers.getMapper(AnnotatedCallMongoEntityMapper.class);

    @Mapping(target = "id.value", source = "id")
    @Mapping(target = "callId.value", source = "callId")
    @Mapping(target = "universityReference", source = "universityId")
    AnnotatedCall toDomain(AnnotatedCallMongoEntity annotatedCallMongoEntity, @Context MongoCrossReferenceResolver resolver);
    List<AnnotatedCall> toDomain(List<AnnotatedCallMongoEntity> annotatedCallMongoEntities, @Context MongoCrossReferenceResolver resolver);

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "callId", source = "callId.value")
    @Mapping(target = "universityId", source = "universityReference.id.value")
    AnnotatedCallMongoEntity fromDomain(AnnotatedCall annotatedCall);
    List<AnnotatedCallMongoEntity> fromDomain(List<AnnotatedCall> annotatedCalls);


    @Mapping(target = "call", source = "callId", qualifiedByName = "resolveCallDTO")
    @Mapping(target = "universityReference", source = "universityId")
    AnnotatedCallDTO toDTO(AnnotatedCallMongoEntity annotatedCall, @Context MongoCrossReferenceResolver resolver);
    List<AnnotatedCallDTO> toDTO(List<AnnotatedCallMongoEntity> annotatedCalls, @Context MongoCrossReferenceResolver resolver);

    @Named("resolveCallDTO")
    default CallDTO resolveCallDTO(ObjectId callMongoEntityId, @Context MongoCrossReferenceResolver resolver) {
        return resolver.resolveCall(callMongoEntityId);
    }

}