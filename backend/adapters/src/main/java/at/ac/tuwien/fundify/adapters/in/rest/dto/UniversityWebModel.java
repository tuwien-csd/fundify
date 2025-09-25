package at.ac.tuwien.fundify.adapters.in.rest.dto;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record UniversityWebModel(
        @Schema(required = true) String id,
        String risId,
        @Schema(required = true) List<TranslatedTextWebModel> name,
        String acronym,
        String emailDomain,
        String website,
        String submissionSystem,
        String phone,
        String crossRefDoi,
        PostAddressWebModel postAddress
) {
}