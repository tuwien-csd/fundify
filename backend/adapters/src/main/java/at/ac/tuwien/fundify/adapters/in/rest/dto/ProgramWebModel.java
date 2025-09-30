package at.ac.tuwien.fundify.adapters.in.rest.dto;

import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.ECareerStageWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.EEntryOriginWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.EFundingCharacteristicWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.EFundingSchemeWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.ELegalTypeWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.EPublicationStatusWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.ETargetGroupWebModel;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record ProgramWebModel(
        // required fields according to the funding API specification v1.1
        @Schema(required = true)
        @Null(groups = ValidationGroups.Post.class)
        @NotNull(groups = ValidationGroups.Put.class)
        String id,

        @Schema(required = true) @NotNull
        EFundingSchemeWebModel fundingScheme,
        @Schema(required = true) @NotNull @Valid @Size(min = 1)
        List<TranslatedTextWebModel> name,
        @Schema(required = true) @NotNull @Valid
        FunderRefWebModel funder,
        @Schema(required = true) @NotNull @Size(min = 1)
        List<EFundingCharacteristicWebModel> characteristics,
        @Schema(required = true) @NotNull @Size(min = 1)
        List<ETargetGroupWebModel> targetGroups,
        @Schema(required = true) @NotNull @Size(min = 1)
        List<StandardizedSubjectWebModel> subjects,
        @Schema(required = true) @NotNull
        ELegalTypeWebModel legalType,

        // optional fields according to the funding API specification v1.1
        @Valid
        List<IdentifierWebModel> identifiers,
        String acronym,
        @Valid
        List<TranslatedTextWebModel> description,
        List<ECareerStageWebModel> careerStages,
        List<String> website,
        @Valid
        List<List<TranslatedTextWebModel>> programTracks,
        @Valid
        DateRangeWebModel duration,

        // internal / metadata fields
        @Schema(required = true) @NotNull
        EPublicationStatusWebModel status,
        @Schema(required = true)
        EEntryOriginWebModel entryOrigin,
        @Schema(required = true)
        LocalDateTime registrationDate,
        @Schema(required = true)
        LocalDateTime lastSync,
        @Schema(required = true)
        String risId
) implements WebModel {

    public String publisherId() {
        return funder != null ? funder.id() : null;
    }
}