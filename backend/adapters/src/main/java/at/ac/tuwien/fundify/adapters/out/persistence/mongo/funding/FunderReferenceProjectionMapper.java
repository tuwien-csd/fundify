package at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.fundify.adapters.in.rest.mapper.CommonIdMapper;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.MongoEntityIdMapper;
import at.ac.tuwien.fundify.domain.funding.FunderReference;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        CommonIdMapper.class,
        MongoEntityIdMapper.class
})
public interface FunderReferenceProjectionMapper {

    FunderReferenceProjectionMapper INSTANCE = Mappers.getMapper(FunderReferenceProjectionMapper.class);

    @Mapping(target = "id", source = "_id")
    FunderReference toFundingDomain(FunderReferenceProjection source);
    List<FunderReference> toFundingDomain(List<FunderReferenceProjection> source);

    @Mapping(target = "_id", source = "id")
    FunderReferenceProjection fromFundingDomain(FunderReference source);
    List<FunderReferenceProjection> fromFundingDomain(List<FunderReference> source);

}
