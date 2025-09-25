package at.ac.tuwien.refop.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.refop.adapters.in.rest.mapper.CommonIdMapper;
import at.ac.tuwien.refop.adapters.out.persistence.mongo.common.MongoEntityIdMapper;
import at.ac.tuwien.refop.domain.funding.CallReference;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        CommonIdMapper.class,
        MongoEntityIdMapper.class
})
public interface CallReferenceProjectionMapper {

    CallReferenceProjectionMapper INSTANCE = Mappers.getMapper(CallReferenceProjectionMapper.class);

    @Mapping(target = "id.value", source = "_id")
    @Mapping(target = "funder.id.value", source = "funderId")
    @Mapping(target = "partOf", source = "partOfId")
    CallReference toDomain(CallReferenceProjection source);
    List<CallReference> toDomain(List<CallReferenceProjection> source);
}
