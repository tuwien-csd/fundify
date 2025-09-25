package at.ac.tuwien.refop.adapters.in.rest.mapper;

import at.ac.tuwien.refop.adapters.in.rest.dto.FunderCreateWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.FunderWebModel;
import at.ac.tuwien.refop.domain.funding.Funder;
import at.ac.tuwien.refop.domain.funding.FunderCreate;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(
        uses = {
                CommonIdMapper.class,
                TranslatedTextWebModelMapper.class
        })
public interface FunderWebModelMapper {

    FunderWebModelMapper INSTANCE = Mappers.getMapper(FunderWebModelMapper.class);

    @Mapping(target = "emailDomain", ignore = true)
    Funder toDomain(FunderWebModel source);
    List<Funder> toDomain(List<FunderWebModel> source);

    @Mapping(target = "emailDomain", ignore = true)
    FunderCreate toDomain(FunderCreateWebModel source);

    FunderWebModel fromDomain(Funder source);
    List<FunderWebModel> fromDomain(List<Funder> source);
}



