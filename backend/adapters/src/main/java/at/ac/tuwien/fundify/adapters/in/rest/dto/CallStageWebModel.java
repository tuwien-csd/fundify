package at.ac.tuwien.fundify.adapters.in.rest.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;


public record CallStageWebModel(
        int number,
        @Schema(required = true) @NotNull
        DateRangeWebModel duration,
        @Schema(required = false)
        List<TranslatedTextWebModel> description
) {
}