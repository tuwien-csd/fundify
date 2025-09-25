package at.ac.tuwien.fundify.adapters.common.ris.mapper;

import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisAnnotatedCall;
import at.ac.tuwien.fundify.domain.dto.AnnotatedCallDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {
        RisCallAnnotationMapper.class,
        RisCallMapper.class
})
public interface RisAnnotatedCallMapper {

    RisAnnotatedCallMapper INSTANCE = Mappers.getMapper(RisAnnotatedCallMapper.class);

    @Mapping(target = "call", source = "call")
    @Mapping(target = "annotation", source = "annotation")
    RisAnnotatedCall fromDTO(AnnotatedCallDTO dto);
    List<RisAnnotatedCall> fromDTO(List<AnnotatedCallDTO> dtos);

}
