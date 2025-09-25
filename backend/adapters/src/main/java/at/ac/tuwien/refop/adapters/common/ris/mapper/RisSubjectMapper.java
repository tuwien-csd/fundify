package at.ac.tuwien.refop.adapters.common.ris.mapper;

import at.ac.tuwien.refop.domain.funding.vo.StandardizedSubject;
import at.ac.tuwien.refop.adapters.common.ris.model.v1.RisSubject;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RisSubjectMapper {

    RisSubjectMapper INSTANCE = Mappers.getMapper(RisSubjectMapper.class);

    @Mapping(target = "level", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "code", source = "value", qualifiedByName = "toStandardizedCode")
    StandardizedSubject toDomain(RisSubject subject);
    List<StandardizedSubject> toDomain(List<RisSubject> subject);

    @AfterMapping
    default List<StandardizedSubject> removeDuplicates(@MappingTarget List<StandardizedSubject> target) {
        return target.stream().distinct().toList();

    }

    @Mapping(target = "fraction", ignore = true)
    @Mapping(target = "value", source = "code")
    RisSubject fromDomain(StandardizedSubject standartizedSubject);
    List<RisSubject> fromDomain(List<StandardizedSubject> standartizedSubject);

    @Named("toStandardizedCode")
    default String toStandardizedCode(String value) {
        if (value == null) {
            return "";
        }
        return value.length() > 3 ? value.substring(0, 3) : value;
    }
}
