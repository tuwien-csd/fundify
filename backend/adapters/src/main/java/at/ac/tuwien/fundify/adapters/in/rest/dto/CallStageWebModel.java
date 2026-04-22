package at.ac.tuwien.fundify.adapters.in.rest.dto;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;


public record CallStageWebModel(
        int number,
        DateRangeWebModel duration,
        List<TranslatedTextWebModel> description
) {
}