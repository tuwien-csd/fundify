package at.ac.tuwien.fundify.adapters.in.rest.dto;

import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.EUpdateSourceWebModel;
import java.time.LocalDateTime;
import java.util.List;

public record CallVersionWebModel(
    String id,
    String callId,
    LocalDateTime versionedAt,
    EUpdateSourceWebModel updateSource,
    List<TranslatedTextWebModel> name,
    List<TranslatedTextWebModel> description,
    List<TranslatedTextWebModel> eligibleApplicants,
    List<CallStageWebModel> callStages,
    MonetaryNumberWebModel callVolumeAmount,
    List<String> website
) {}
