package at.ac.tuwien.fundify.adapters.in.rest.mapper;

import at.ac.tuwien.fundify.adapters.in.rest.dto.UniversityRefWebModel;
import at.ac.tuwien.fundify.domain.annotating.UniversityReference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PublisherRefWebModelMapper {

    PublisherRefWebModelMapper INSTANCE = Mappers.getMapper(PublisherRefWebModelMapper.class);

    @Mapping(target = "id", source = "id.value")
    UniversityRefWebModel fromDomain(UniversityReference source);
}
