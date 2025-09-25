package at.ac.tuwien.refop.adapters.in.rest.mapper;

import at.ac.tuwien.refop.adapters.in.rest.dto.UniversityCreateWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.UniversityWebModel;
import at.ac.tuwien.refop.domain.annotating.University;
import at.ac.tuwien.refop.domain.annotating.UniversityCreate;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(
        uses = {
                CommonIdMapper.class,
                TranslatedTextWebModelMapper.class
        })
public interface UniversityWebModelMapper {
    UniversityWebModelMapper INSTANCE = Mappers.getMapper(UniversityWebModelMapper.class);

    @Mapping(target = "emailDomain", ignore = true)
    University toDomain(UniversityWebModel source);

    @Mapping(target = "emailDomain", ignore = true)
    UniversityCreate toDomain(UniversityCreateWebModel source);
    List<University> toDomain(List<UniversityWebModel> source);

    UniversityWebModel fromDomain(University source);
    List<UniversityWebModel> fromDomain(List<University> source);

}
