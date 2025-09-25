package at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating;

import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.MongoEntityIdMapper;
import at.ac.tuwien.fundify.domain.annotating.AnnotatingFunderReference;
import at.ac.tuwien.fundify.domain.annotating.AnnotatingProgramReference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        MongoEntityIdMapper.class
})
public interface AnnotatingReferenceProjectionMapper {

    AnnotatingReferenceProjectionMapper INSTANCE = Mappers.getMapper(AnnotatingReferenceProjectionMapper.class);

    @Mapping(target = "id.value", source = "_id")
    AnnotatingFunderReference toAnnotatingFunderReference(AnnotatingFunderReferenceProjection projection);

    @Mapping(target = "id.value", source = "_id")
    AnnotatingProgramReference toAnnotatingProgramReference(AnnotatingProgramReferenceProjection projection);

}
