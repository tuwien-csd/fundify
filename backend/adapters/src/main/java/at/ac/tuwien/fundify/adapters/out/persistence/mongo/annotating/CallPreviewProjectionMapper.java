package at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating;

import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.MongoCrossReferenceResolver;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.MongoEntityIdMapper;
import at.ac.tuwien.fundify.domain.annotating.AnnotatingFunderReference;
import at.ac.tuwien.fundify.domain.annotating.AnnotatingProgramReference;
import at.ac.tuwien.fundify.domain.dto.CallPreviewDTO;
import org.bson.types.ObjectId;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        MongoEntityIdMapper.class
})
public interface CallPreviewProjectionMapper {

    CallPreviewProjectionMapper INSTANCE = Mappers.getMapper(CallPreviewProjectionMapper.class);

    @Mapping(target = "id", source = "_id")
    @Mapping(target = "partOf", source ="partOfId", qualifiedByName = "toAnnotatingProgramReference")
    @Mapping(target = "funder", source = "funderId", qualifiedByName = "toAnnotatingFunderReference")
    CallPreviewDTO toDTO(CallPreviewProjection source, @Context MongoCrossReferenceResolver resolver);


    @Named("toAnnotatingProgramReference")
    default AnnotatingProgramReference toAnnotatingProgramReference(ObjectId programId, @Context MongoCrossReferenceResolver resolver) {
        return resolver.resolveAnnotatingProgramReference(programId);
    }

    @Named("toAnnotatingFunderReference")
    default AnnotatingFunderReference toAnnotatingFunderReference(ObjectId funderId, @Context MongoCrossReferenceResolver resolver) {
        return resolver.resolveAnnotatingFunderReference(funderId);
    }
}
