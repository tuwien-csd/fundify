package at.ac.tuwien.refop.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.refop.adapters.in.rest.mapper.CommonIdMapper;
import at.ac.tuwien.refop.adapters.out.persistence.mongo.common.MongoEntityIdMapper;
import at.ac.tuwien.refop.domain.funding.Funder;
import at.ac.tuwien.refop.domain.funding.FunderCreate;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        CommonIdMapper.class,
        MongoEntityIdMapper.class}
)
public interface FunderMongoEntityMapper {

    FunderMongoEntityMapper INSTANCE = Mappers.getMapper(FunderMongoEntityMapper.class);

    @Mapping(target = "id.value", source = "id")
    Funder toDomain(FunderMongoEntity funderMongoEntity);
    List<Funder> toDomain(List<FunderMongoEntity> funderMongoEntities);

    @Mapping(target = "id", source = "id.value")
    FunderMongoEntity fromDomain(Funder funder);
    List<FunderMongoEntity> fromDomain(List<Funder> funders);

    FunderMongoEntity fromDomain(FunderCreate funderCreate);

}
