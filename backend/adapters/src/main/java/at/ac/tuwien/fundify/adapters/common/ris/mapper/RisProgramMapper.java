package at.ac.tuwien.fundify.adapters.common.ris.mapper;

import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFunder;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFundingType;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisOrganisationFundingRoleEnum;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisProgramme;
import at.ac.tuwien.fundify.domain.funding.FunderReference;
import at.ac.tuwien.fundify.domain.funding.Program;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        CommonRisMappingConfig.class,
        RisTextMapper.class,
        RisIdentifierMapper.class,
        RisSubjectMapper.class,
        RisFunderRefMapper.class
})
public interface RisProgramMapper {

    RisProgramMapper INSTANCE = Mappers.getMapper(RisProgramMapper.class);

    @Mapping(target = "status",  ignore = true)
    @Mapping(target = "entryOrigin", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalIdentifier", source = ".")
    @Mapping(target = "duration.start", source = "startDate")
    @Mapping(target = "duration.end", source = "endDate")
    @Mapping(target = "funder", source = "funder", qualifiedByName = "risFunderListToFunderRef")
    @Mapping(target = "programTracks", source = "programmeTracks")
    Program toDomain(RisProgramme source, @Context String memberId);
    List<Program> toDomain(List<RisProgramme> source, @Context String memberId);

    @Named("risFunderListToFunderRef")
    default FunderReference risFunderListToFunderRef(List<RisFunder> funder, @Context String memberId) {
        if (funder == null || funder.isEmpty()) {
            return null;
        }
        return funder.stream()
                .filter(f -> f.getFunderType() == RisOrganisationFundingRoleEnum.EXECUTIVE_ORGANISATION)
                .findFirst()
                .map(f -> RisFunderRefMapper.INSTANCE.toDomain(f, memberId))
                .orElse(null);
    }

    @Mapping(target = "type", ignore = true) // see AfterMapping
    @Mapping(target = "id", source = "externalIdentifier.risId.id")
    @Mapping(target = "startDate", source = "duration.start")
    @Mapping(target = "endDate", source = "duration.end")
    @Mapping(target = "funder", source = "funder", qualifiedByName = "funderRefToRisFunderList")
    @Mapping(target = "programmeTracks", source = "programTracks")
    @Mapping(target = "identifiers", source = "externalIdentifier.identifiers")
    RisProgramme fromDomain(Program source);
    List<RisProgramme> fromDomain(List<Program> source);

    @Named("funderRefToRisFunderList")
    default List<RisFunder> funderRefToRisFunderList(FunderReference funder) {
        if (funder == null) {
            return new ArrayList<>();
        }
        return List.of(RisFunderRefMapper.INSTANCE.fromDomain(funder).funderType(RisOrganisationFundingRoleEnum.EXECUTIVE_ORGANISATION));
    }

    @AfterMapping
    default void assignFundingType(@MappingTarget RisProgramme target) {
        target.setType(RisFundingType.PROGRAMME);
    }
}
