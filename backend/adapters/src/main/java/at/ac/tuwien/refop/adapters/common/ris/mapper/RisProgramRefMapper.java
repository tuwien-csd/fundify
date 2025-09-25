package at.ac.tuwien.refop.adapters.common.ris.mapper;

import at.ac.tuwien.refop.domain.funding.ProgramReference;
import at.ac.tuwien.refop.adapters.common.ris.model.v1.RisBaseFunding;
import at.ac.tuwien.refop.adapters.common.ris.model.v1.RisFundingType;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {
        CommonRisMappingConfig.class,
        RisTextMapper.class,
        RisIdentifierMapper.class
})
public interface RisProgramRefMapper {

    RisProgramRefMapper INSTANCE = Mappers.getMapper(RisProgramRefMapper.class);

    @Mapping(target = "website", ignore = true)
    @Mapping(target = "funder", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "risId", source = "id", qualifiedByName = "fundingRisIdFromId")
    ProgramReference toDomain(RisBaseFunding source, @Context String memberId);
    List<ProgramReference> toDomain(List<RisBaseFunding> source, @Context String memberId);

    @Mapping(target = "type", ignore = true)
    @Mapping(target = "id", source = "risId.id")
    RisBaseFunding fromDomain(ProgramReference source);

    @AfterMapping
    default void assignType(@MappingTarget RisBaseFunding target) {
        target.setType(RisFundingType.PROGRAMME);
    }
}
