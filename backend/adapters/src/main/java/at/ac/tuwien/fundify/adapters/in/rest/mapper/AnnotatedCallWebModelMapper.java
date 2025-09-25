package at.ac.tuwien.fundify.adapters.in.rest.mapper;

import at.ac.tuwien.fundify.adapters.in.rest.dto.AnnotatedCallWebModel;
import at.ac.tuwien.fundify.domain.dto.AnnotatedCallDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        CallPreviewWebModelMapper.class,
        CallAnnotationWebModelMapper.class,
        CommonIdMapper.class})
public interface AnnotatedCallWebModelMapper {
    AnnotatedCallWebModelMapper INSTANCE = Mappers.getMapper(AnnotatedCallWebModelMapper.class);

    @Mapping(target = "callPreview", source = "call")
    @Mapping(target = "university", source = "universityReference")
    AnnotatedCallWebModel toWebModel(AnnotatedCallDTO source);
    List<AnnotatedCallWebModel> toWebModels(List<AnnotatedCallDTO> source);

}
