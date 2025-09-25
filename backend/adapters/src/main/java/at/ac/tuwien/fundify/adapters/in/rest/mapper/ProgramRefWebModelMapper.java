package at.ac.tuwien.fundify.adapters.in.rest.mapper;

import at.ac.tuwien.fundify.adapters.in.rest.dto.ProgramRefWebModel;
import at.ac.tuwien.fundify.domain.funding.ProgramReference;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses =
        {CommonIdMapper.class,
        TranslatedTextWebModelMapper.class
        })
public interface ProgramRefWebModelMapper {

    ProgramRefWebModelMapper INSTANCE = Mappers.getMapper(ProgramRefWebModelMapper.class);

    @Mapping(target = "website", ignore = true)
    @Mapping(target = "funder", ignore = true)
    ProgramReference toDomain(ProgramRefWebModel source);
    List<ProgramReference> toDomain(List<ProgramRefWebModel> source);

    @Mapping(target = "type", ignore = true)
    ProgramRefWebModel fromDomain(ProgramReference source);
    List<ProgramRefWebModel> fromDomain(List<ProgramReference> source);


}
