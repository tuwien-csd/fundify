package at.ac.tuwien.refop.adapters.common.ris.mapper;

import at.ac.tuwien.refop.domain.funding.FunderReference;
import at.ac.tuwien.refop.adapters.common.ris.model.v1.RisFunder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(uses = {CommonRisMappingConfig.class, RisTextMapper.class, RisIdentifierMapper.class})
public interface RisFunderRefMapper {

    RisFunderRefMapper INSTANCE = Mappers.getMapper(RisFunderRefMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "risId", ignore = true)
    @Mapping(target = "emailDomain", ignore = true)
    @Mapping(target = "website", ignore = true)
    @Mapping(target = "name", source = "funder.name")
    @Mapping(target = "acronym", source = "funder.acronym")
    @Mapping(target = "identifiers", source = "funder.identifiers")
    FunderReference toDomain(RisFunder source);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "emailDomain", ignore = true)
    @Mapping(target = "risId", source ="funder.id", qualifiedByName = "orgunitRisIdfromId")
    @Mapping(target = "website", ignore = true)
    @Mapping(target = "name", source = "funder.name")
    @Mapping(target = "acronym", source = "funder.acronym")
    @Mapping(target = "identifiers", source = "funder.identifiers")
    FunderReference toDomain(RisFunder source, @Context String memberId);

    @Mapping(target = "funderType", ignore = true) // not defined in isolation, only in context of funding
    @Mapping(target = "funder.id", source = "risId.id")
    @Mapping(target = "funder.name", source = "name")
    @Mapping(target = "funder.acronym", source = "acronym")
    @Mapping(target = "funder.identifiers", source = "identifiers")
    RisFunder fromDomain(FunderReference source);
}
