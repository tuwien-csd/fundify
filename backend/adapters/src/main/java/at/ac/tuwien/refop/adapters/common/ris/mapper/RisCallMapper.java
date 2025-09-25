package at.ac.tuwien.refop.adapters.common.ris.mapper;

import at.ac.tuwien.refop.adapters.common.ris.model.v1.RisCall;
import at.ac.tuwien.refop.adapters.common.ris.model.v1.RisFunder;
import at.ac.tuwien.refop.adapters.common.ris.model.v1.RisFundingType;
import at.ac.tuwien.refop.adapters.common.ris.model.v1.RisVolume;
import at.ac.tuwien.refop.domain.dto.CallDTO;
import at.ac.tuwien.refop.domain.funding.Call;
import at.ac.tuwien.refop.domain.funding.FunderReference;
import at.ac.tuwien.refop.domain.funding.vo.MonetaryNumber;
import at.ac.tuwien.refop.domain.funding.vo.enums.ECallType;
import at.ac.tuwien.refop.domain.funding.vo.enums.ECurrency;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        CommonRisMappingConfig.class,
        RisTextMapper.class,
        RisIdentifierMapper.class,
        RisSubjectMapper.class,
        RisCallStageMapper.class,
        RisFunderRefMapper.class,
        RisProgramRefMapper.class
})
public interface RisCallMapper {

    RisCallMapper INSTANCE = Mappers.getMapper(RisCallMapper.class);

    @Mapping(target = "entryOrigin", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "externalIdentifier", source = ".")
    @Mapping(target = "projectStartDetails", source = "projectStart")
    @Mapping(target = "fundingType", source = "type")
    @Mapping(target = "jointCallPartner", source = "funder", qualifiedByName = "funderToJointCallPartner")
    @Mapping(target = "reportingPeriodDetails", source = "reportingPeriods")
    @Mapping(target = "overheadDetails", source = "overheadsDetails")
    @Mapping(target = "maxOverhead", source = "maxOverHeads")
    @Mapping(target = "decisionProcess", source = "decisionProcesses")
    @Mapping(target = "callVolumeAmount", source = "amount")
    Call toDomain(RisCall source, @Context String memberId);
    List<Call> toDomain(List<RisCall> source, @Context String memberId);

    default FunderReference funderToFunderReference(List<RisFunder> funders, @Context String memberId) {
        if (funders == null || funders.isEmpty()) {
            return null;
        }
        return funders.stream()
                .filter(f -> f.getFunderType() == RisFunder.FunderTypeEnum.EXECUTIVE_ORGANISATION)
                .findFirst()
                .map(f -> RisFunderRefMapper.INSTANCE.toDomain(f, memberId))
                .orElse(null);
    }

    @Named("funderToJointCallPartner")
    default List<FunderReference> funderToJointCallPartner(List<RisFunder> funders) {
        if (funders == null || funders.isEmpty()) {
            return Collections.emptyList();
        }
        return funders.stream()
                .filter(f -> f.getFunderType() == RisFunder.FunderTypeEnum.JOINT_CALL_PARTNER)
                .map(RisFunderRefMapper.INSTANCE::toDomain)
                .toList();
    }

    default ECallType fundingTypeToECallType(RisFundingType fundingType) {
        return switch (fundingType) {
            case CALL -> ECallType.CALL;
            case ONGOING_CALL -> ECallType.ONGOING_CALL;
            default -> throw new IllegalArgumentException("Unknown call type: " + fundingType);
        };
    }

    default MonetaryNumber map(RisVolume volume) {
        if (volume == null || volume.getAmount() == null || volume.getCurrency() == null){
            return null;
        }
        return new MonetaryNumber(
                volume.getAmount(),
                ECurrency.valueOf(volume.getCurrency().toUpperCase())
        );
    }

    @Mapping(target = "type", source = "fundingType", qualifiedByName = "eCallTypeToFundingType")
    @Mapping(target = "id", source = "risId.id")
    @Mapping(target = "projectStart", source = "projectStartDetails")
    @Mapping(target = "funder", expression = "java(addFunderReferences(source.getFunder(), source.getJointCallPartner()))")
    @Mapping(target = "reportingPeriods", source = "reportingPeriodDetails")
    @Mapping(target = "overheadsDetails", source = "overheadDetails")
    @Mapping(target = "maxOverHeads", source = "maxOverhead")
    @Mapping(target = "decisionProcesses", source = "decisionProcess")
    @Mapping(target = "amount", source = "callVolumeAmount" , qualifiedByName = "monetaryNumberToVolume")
    RisCall fromDTO(CallDTO source);
    List<RisCall> fromDTO(List<CallDTO> source);

    @Named("addFunderReferences")
    default List<RisFunder> addFunderReferences(FunderReference mainFunder, List<FunderReference> jointCallPartner) {
        List<RisFunder> funders = new ArrayList<>();
        if (mainFunder != null) {
            funders.add(RisFunderRefMapper.INSTANCE
                    .fromDomain(mainFunder)
                    .funderType(RisFunder.FunderTypeEnum.EXECUTIVE_ORGANISATION));
        }
        if (jointCallPartner != null) {
            jointCallPartner.forEach(f -> funders.add(RisFunderRefMapper.INSTANCE
                    .fromDomain(f)
                    .funderType(RisFunder.FunderTypeEnum.JOINT_CALL_PARTNER)));
        }
        return funders;
    }

    @Named("eCallTypeToFundingType")
    default RisFundingType eCallTypeToFundingType(ECallType callType) {
        return switch (callType) {
            case CALL -> RisFundingType.CALL;
            case ONGOING_CALL -> RisFundingType.ONGOING_CALL;
        };
    }

    @Named("monetaryNumberToVolume")
    default RisVolume monetaryNumberToVolume(MonetaryNumber monetaryNumber) {
        if (monetaryNumber == null || monetaryNumber.amount() == null || monetaryNumber.currency() == null) {
            return null;
        }
        return new RisVolume()
                .currency(monetaryNumber.currency().toString())
                .amount(monetaryNumber.amount());
    }
}
