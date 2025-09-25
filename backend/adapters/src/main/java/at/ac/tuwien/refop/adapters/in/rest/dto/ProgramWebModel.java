package at.ac.tuwien.refop.adapters.in.rest.dto;

import at.ac.tuwien.refop.adapters.in.rest.dto.enums.ECareerStageWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.EEntryOriginWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.EFundingCharacteristicWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.EFundingSchemeWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.ELegalTypeWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.EPublicationStatusWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.ETargetGroupWebModel;
import java.time.LocalDateTime;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record ProgramWebModel(

        //meta data fields
        @Schema(required = true)
        String id,
        @Schema(required = true)
        EPublicationStatusWebModel status,
        @Schema(required = true)
        EEntryOriginWebModel entryOrigin,
        @Schema(required = true)
        LocalDateTime registrationDate,
        @Schema(required = true)
        LocalDateTime lastSync,
        // mandatory fields
        @Schema(required = true)
        String risId,
        @Schema(required = true)
        List<TranslatedTextWebModel> name,
        @Schema(required = true)
        List<ETargetGroupWebModel> targetGroups,
        @Schema(required = true)
        List<StandardizedSubjectWebModel> subjects,
        @Schema(required = true)
        List<TranslatedTextWebModel> description,
        @Schema(required = true)
        List<EFundingCharacteristicWebModel> characteristics,
        @Schema(required = true)
        List<String> website,
        @Schema(required = true)
        EFundingSchemeWebModel fundingScheme,
        @Schema(required = true)
        ELegalTypeWebModel legalType,
        @Schema(required = true)
        FunderRefWebModel funder,
        // optional fields
        String acronym,
        List<IdentifierWebModel> identifiers,
        List<List<TranslatedTextWebModel>> programTracks,
        List<ECareerStageWebModel> careerStages,
        DateRangeWebModel duration
) implements WebModel {

    public String publisherId() {
        return funder != null ? funder.id() : null;
    }
}