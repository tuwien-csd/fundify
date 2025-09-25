package at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.fundify.adapters.in.rest.mapper.CommonIdMapper;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.CommonReferenceMapper;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.MongoCrossReferenceResolver;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.MongoEntityIdMapper;
import at.ac.tuwien.fundify.domain.funding.Program;
import java.util.List;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        FunderReferenceProjectionMapper.class,
        CommonIdMapper.class,
        CommonReferenceMapper.class,
        MongoEntityIdMapper.class})
public interface ProgramMongoEntityMapper {

    ProgramMongoEntityMapper INSTANCE = Mappers.getMapper(ProgramMongoEntityMapper.class);

    @Mapping(target = "externalIdentifier.risId", source = "risId")
    @Mapping(target = "externalIdentifier.identifiers", source = "identifiers")
    @Mapping(target = "funder", source = "funderId")
    Program toDomain(ProgramMongoEntity program, @Context MongoCrossReferenceResolver resolver);
    List<Program> toDomain(List<ProgramMongoEntity> program, @Context MongoCrossReferenceResolver resolver);

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "risId", source = "externalIdentifier.risId")
    @Mapping(target = "identifiers", source = "externalIdentifier.identifiers")
    @Mapping(target = "funderId", source = "funder.id.value")
    ProgramMongoEntity fromDomain(Program program);
    List<ProgramMongoEntity> fromDomain(List<Program> programs);
}
