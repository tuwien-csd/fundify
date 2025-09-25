package at.ac.tuwien.fundify.adapters.in.rest.mapper;

import at.ac.tuwien.fundify.adapters.in.rest.dto.ProgramWebModel;
import at.ac.tuwien.fundify.domain.funding.Program;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        CommonIdMapper.class,
        TranslatedTextWebModelMapper.class,
        StandardizedSubjectWebModelMapper.class,
        FunderRefWebModelMapper.class
})
public interface ProgramWebModelMapper {

    ProgramWebModelMapper INSTANCE = Mappers.getMapper(ProgramWebModelMapper.class);

    @Mapping(target = "externalIdentifier.identifiers", source = "identifiers")
    @Mapping(target = "externalIdentifier.risId", source = "risId")
    Program toDomain(ProgramWebModel source);
    List<Program> toDomain(List<ProgramWebModel> source);

    @Mapping(target = "identifiers", source = "externalIdentifier.identifiers")
    @Mapping(target = "risId", source = "externalIdentifier.risId")
    ProgramWebModel fromDomain(Program source);
    List<ProgramWebModel> fromDomain(List<Program> source);
}
