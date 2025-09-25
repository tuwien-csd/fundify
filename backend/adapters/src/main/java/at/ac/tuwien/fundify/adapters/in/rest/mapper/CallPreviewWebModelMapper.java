package at.ac.tuwien.fundify.adapters.in.rest.mapper;

import at.ac.tuwien.fundify.adapters.in.rest.dto.CallPreviewWebModel;
import at.ac.tuwien.fundify.domain.dto.CallDTO;
import at.ac.tuwien.fundify.domain.dto.CallPreviewDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        TranslatedTextWebModelMapper.class,
        CommonIdMapper.class,
        CallStageWebModelMapper.class})
public interface CallPreviewWebModelMapper {

    CallPreviewWebModelMapper INSTANCE = Mappers.getMapper(CallPreviewWebModelMapper.class);

    CallPreviewWebModel toWebModel(CallDTO source);

    CallPreviewWebModel toWebModel(CallPreviewDTO source);

}
