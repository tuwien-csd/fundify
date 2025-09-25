package at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating;

import at.ac.tuwien.fundify.adapters.in.rest.mapper.CommonIdMapper;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.common.MongoEntityIdMapper;
import at.ac.tuwien.fundify.domain.annotating.University;
import at.ac.tuwien.fundify.domain.annotating.UniversityCreate;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        MongoEntityIdMapper.class,
        CommonIdMapper.class
})
public interface UniversityMongoEntityMapper {

    UniversityMongoEntityMapper INSTANCE = Mappers.getMapper(UniversityMongoEntityMapper.class);

    @Mapping(target = "id.value", source = "id")
    @Mapping(target = "risId", source = "risId")
    University toDomain(UniversityMongoEntity universityMongoEntity);
    List<University> toDomain(List<UniversityMongoEntity> universityMongoEntities);

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "risId", source = "risId")
    UniversityMongoEntity fromDomain(University university);

    UniversityMongoEntity fromDomain(UniversityCreate universityCreate);

}
