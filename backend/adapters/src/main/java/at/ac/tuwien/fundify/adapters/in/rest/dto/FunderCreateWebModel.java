package at.ac.tuwien.fundify.adapters.in.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record FunderCreateWebModel(
    @Schema(required = true)
    @NotNull @Size(min = 1) @Valid
    List<TranslatedTextWebModel> name,
    @Schema(required = true)
    String website,
    @Schema(required = true)
    String acronym,
    String risId,
    @Valid List<IdentifierWebModel> identifiers,
    String submissionSystem,
    String phone,
    String crossRefDoi,
    @Valid PostAddressWebModel postAddress,
    Boolean externallyAdministered
) {

}