package at.ac.tuwien.fundify.adapters.in.rest.mapper;

import at.ac.tuwien.fundify.adapters.in.rest.dto.CallOwnerWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.CallUpdateWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.CallWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.ESubscriptionStatusWebModel;
import at.ac.tuwien.fundify.domain.common.CallOwner;
import at.ac.tuwien.fundify.domain.common.ESubscriptionStatus;
import at.ac.tuwien.fundify.domain.dto.CallDTO;
import at.ac.tuwien.fundify.domain.funding.Call;
import at.ac.tuwien.fundify.domain.funding.CallUpdate;
import java.util.List;
import java.util.Optional;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        CommonIdMapper.class,
        TranslatedTextWebModelMapper.class,
        StandardizedSubjectWebModelMapper.class,
        CallStageWebModelMapper.class,
        ProgramRefWebModelMapper.class,
        FunderRefWebModelMapper.class,
})
public interface CallWebModelMapper {

    CallWebModelMapper INSTANCE = Mappers.getMapper(CallWebModelMapper.class);

    @Mapping(target = "externalIdentifier.identifiers", source = "identifiers")
    @Mapping(target = "externalIdentifier.risId", source = "risId")
    @Mapping(target = "callOwner", ignore = true)
    Call toDomain(CallWebModel source);

    List<Call> toDomain(List<CallWebModel> source);

    @Mapping(target = "externalIdentifier.identifiers", source = "identifiers")
    @Mapping(target = "externalIdentifier.risId", source = "risId")
    @Mapping(target = "callOwner", ignore = true)
    CallUpdate toDomain(CallUpdateWebModel source);

    @Mapping(target = "identifiers", source = "externalIdentifier.identifiers")
    @Mapping(target = "risId", source = "externalIdentifier.risId")
    @Mapping(target = "callOwner", source = "callOwner", qualifiedByName = "mapOwnerToWeb")
    @Mapping(target = "subscriptionStatus", expression = "java(this.isSubscribed(source, userId))")
    CallWebModel fromDomain(Call source, @Context String userId);

    List<CallWebModel> fromDomain(List<Call> source, @Context String userId);

    @Named("mapOwnerToWeb")
    default CallOwnerWebModel mapOwnerToWeb(CallOwner owner) {
        if (owner == null || owner.getCallOwnerKind() == null) return null;
        return new CallOwnerWebModel(
                owner.getCallOwnerKind().name(),
                owner.getAcronym(),
                owner.getOwnerId()
        );
    }

    CallWebModel fromDTO(CallDTO source);
    List<CallWebModel> fromDTOs(List<CallDTO> source);

    ESubscriptionStatus fromDto(ESubscriptionStatusWebModel source);

  default ESubscriptionStatusWebModel isSubscribed(Call source, @Context String userId) {

    return Optional.ofNullable(source.getSubscriptions())
        .map(subs -> subs.stream().anyMatch(sub -> sub.id().equals(userId)))
        .filter(it -> it)
        .map(it -> ESubscriptionStatusWebModel.SUBSCRIBED)
        .orElse(ESubscriptionStatusWebModel.UNSUBSCRIBED);
  }
}

