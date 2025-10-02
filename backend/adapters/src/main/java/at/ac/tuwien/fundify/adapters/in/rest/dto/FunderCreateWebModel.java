package at.ac.tuwien.fundify.adapters.in.rest.dto;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record FunderCreateWebModel(
    @Schema(required = true)
    List<TranslatedTextWebModel> name,
    @Schema(required = true)
    String website,
    @Schema(required = true)
    String acronym,
    String risId,
    List<IdentifierWebModel> identifiers,
    String submissionSystem,
    String phone,
    String crossRefDoi,
    PostAddressWebModel postAddress,
    Boolean externallyAdministered
) {

}