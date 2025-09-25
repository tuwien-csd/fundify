package at.ac.tuwien.fundify.adapters.common.ris.mapper;

import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisCallStage;
import at.ac.tuwien.fundify.domain.funding.vo.CallStage;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {CommonRisMappingConfig.class, RisTextMapper.class })
public interface RisCallStageMapper {

    RisCallStageMapper INSTANCE = Mappers.getMapper(RisCallStageMapper.class);

    @Mapping(target = "duration.start", source = "callStageStart")
    @Mapping(target = "duration.end", source = "callStageEnd")
    @Mapping(target = "description", source = "callStageDescription")
    CallStage toDomain(RisCallStage source);
    List<CallStage> toDomain(List<CallStage> source);

    @Mapping(target = "callStageStart", source = "duration.start")
    @Mapping(target = "callStageEnd", source = "duration.end")
    @Mapping(target = "callStageDescription", source = "description")
    RisCallStage fromDomain(CallStage source);
    List<RisCallStage> fromDomain(List<CallStage> source);
}
