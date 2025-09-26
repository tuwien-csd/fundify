package at.ac.tuwien.fundify.adapters.in.rest.dto;

import java.util.List;
import io.smallrye.common.constraint.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;


public record CallStageWebModel(
        int number,
        @Schema(required = true) @NotNull
        DateRangeWebModel duration,
        @Schema(required = true) @NotNull
        List<TranslatedTextWebModel> description
) {
}