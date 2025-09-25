package at.ac.tuwien.fundify.adapters.in.rest.mapper;

import at.ac.tuwien.fundify.adapters.in.rest.dto.CallStageWebModel;
import at.ac.tuwien.fundify.domain.funding.vo.CallStage;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {TranslatedTextWebModelMapper.class})
public interface CallStageWebModelMapper {
    CallStageWebModelMapper INSTANCE = Mappers.getMapper(CallStageWebModelMapper.class);

    CallStageWebModel fromDomain(CallStage source);
    List<CallStageWebModel> fromDomain(List<CallStage> source);

    CallStage toDomain(CallStageWebModel source);
    List<CallStage> toDomain(List<CallStageWebModel> source);
}
