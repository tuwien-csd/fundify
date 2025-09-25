package at.ac.tuwien.fundify.adapters.in.rest.mapper;

import at.ac.tuwien.fundify.adapters.in.rest.constants.SubjectStore;
import at.ac.tuwien.fundify.adapters.in.rest.dto.StandardizedSubjectWebModel;
import at.ac.tuwien.fundify.domain.funding.vo.StandardizedSubject;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StandardizedSubjectWebModelMapper {

    StandardizedSubjectWebModelMapper INSTANCE = Mappers.getMapper(StandardizedSubjectWebModelMapper.class);

    @Mapping(target = "title", ignore = true)
    @Mapping(target = "level", ignore = true)
    StandardizedSubject toDomain(StandardizedSubjectWebModel source);
    List<StandardizedSubject> toDomain(List<StandardizedSubjectWebModel> source);

    @Mapping(target = "title", ignore = true)
    @Mapping(target = "level", ignore = true)
    StandardizedSubjectWebModel fromDomain(StandardizedSubject source);
    List<StandardizedSubjectWebModel> fromDomain(List<StandardizedSubject> source);

    @AfterMapping
    default StandardizedSubjectWebModel fetchSubjectFromStore(@MappingTarget StandardizedSubjectWebModel target) {
        return SubjectStore.getSubjectByCode(target.code());
    }
}
