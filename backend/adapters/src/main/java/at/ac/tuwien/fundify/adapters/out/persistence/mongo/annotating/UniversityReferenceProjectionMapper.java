package at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating;

import at.ac.tuwien.fundify.adapters.in.rest.mapper.CommonIdMapper;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.MongoEntityIdMapper;
import at.ac.tuwien.fundify.domain.annotating.UniversityReference;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        MongoEntityIdMapper.class,
        CommonIdMapper.class})
public interface UniversityReferenceProjectionMapper {

    UniversityReferenceProjectionMapper INSTANCE = Mappers.getMapper(UniversityReferenceProjectionMapper.class);

    @Mapping(target = "id.value", source = "_id")
    UniversityReference toDomain(UniversityReferenceProjection universityReferenceProjection);
    List<UniversityReference> toDomain(List<UniversityReferenceProjection> universityReferenceProjections);
}
