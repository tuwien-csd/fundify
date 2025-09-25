package at.ac.tuwien.fundify.adapters.in.rest.mapper;

import at.ac.tuwien.fundify.adapters.in.rest.dto.FunderRefWebModel;
import at.ac.tuwien.fundify.domain.funding.FunderReference;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(
        uses = {
                CommonIdMapper.class,
                TranslatedTextWebModelMapper.class
        },
        nullValueCheckStrategy = org.mapstruct.NullValueCheckStrategy.ALWAYS
)
public interface FunderRefWebModelMapper {

    FunderRefWebModelMapper INSTANCE = Mappers.getMapper(FunderRefWebModelMapper.class);

    @Mapping(target = "website", ignore = true)
    @Mapping(target = "emailDomain", ignore = true)
    FunderReference toDomain(FunderRefWebModel source);
    List<FunderReference> toDomain(List<FunderRefWebModel> source);

    @Mapping(target = "type", ignore = true)
    FunderRefWebModel fromDomain(FunderReference source);
    List<FunderRefWebModel> fromDomain(List<FunderReference> source);
}
