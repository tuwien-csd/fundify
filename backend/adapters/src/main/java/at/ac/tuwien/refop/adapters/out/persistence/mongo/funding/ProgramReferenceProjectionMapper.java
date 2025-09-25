package at.ac.tuwien.refop.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.refop.adapters.in.rest.mapper.CommonIdMapper;
import at.ac.tuwien.refop.adapters.out.persistence.mongo.common.MongoEntityIdMapper;
import at.ac.tuwien.refop.domain.funding.ProgramReference;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(uses = {
        CommonIdMapper.class,
        MongoEntityIdMapper.class
})
public interface ProgramReferenceProjectionMapper {

    ProgramReferenceProjectionMapper INSTANCE = Mappers.getMapper(ProgramReferenceProjectionMapper.class);

    @Mapping(target = "id.value", source = "_id")
    @Mapping(target = "funder.id", source = "funderId")
    ProgramReference toFundingDomainUnresolved(ProgramReferenceProjection source);
    List<ProgramReference> toFundingDomainUnresolved(List<ProgramReferenceProjection> source);

}
