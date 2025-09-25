package at.ac.tuwien.fundify.adapters.in.rest.dto;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record FunderWebModel(@Schema(required = true) String id,
                             @Schema(required = true) List<TranslatedTextWebModel> name,
                             String risId,
                             @Schema(required = true) String website,
                             String acronym,
                             List<IdentifierWebModel> identifiers,
                             String submissionSystem,
                             String phone,
                             String crossRefDoi,
                             PostAddressWebModel postAddress,
                             Boolean externallyAdministered) {

}