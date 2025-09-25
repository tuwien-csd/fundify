package at.ac.tuwien.fundify.adapters.common.ris.mapper;

import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisCallAnnotation;
import at.ac.tuwien.fundify.domain.dto.CallAnnotationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {RisTextMapper.class })
public interface RisCallAnnotationMapper {

    RisCallAnnotationMapper INSTANCE = Mappers.getMapper(RisCallAnnotationMapper.class);

    RisCallAnnotation fromDTO(CallAnnotationDTO dto);
}
