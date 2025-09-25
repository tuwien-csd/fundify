package at.ac.tuwien.fundify.adapters.in.rest.mapper;

import at.ac.tuwien.fundify.adapters.in.rest.dto.CallAnnotationWebModel;
import at.ac.tuwien.fundify.domain.annotating.CallAnnotation;
import at.ac.tuwien.fundify.domain.dto.CallAnnotationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = TranslatedTextWebModelMapper.class)
public interface CallAnnotationWebModelMapper {
    CallAnnotationWebModelMapper INSTANCE = Mappers.getMapper(CallAnnotationWebModelMapper.class);

    CallAnnotationWebModel fromDomain(CallAnnotation callAnnotation);
    CallAnnotationWebModel fromDTO(CallAnnotationDTO callAnnotation);

    CallAnnotation toDomain(CallAnnotationWebModel callAnnotationWebModel);
}
