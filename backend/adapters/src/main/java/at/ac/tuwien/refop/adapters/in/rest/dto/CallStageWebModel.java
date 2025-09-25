package at.ac.tuwien.refop.adapters.in.rest.dto;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;


public record CallStageWebModel(
        int number,
        @Schema(required = true)
        DateRangeWebModel duration,
        @Schema(required = true)
        List<TranslatedTextWebModel> description
) {
}