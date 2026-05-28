package at.ac.tuwien.fundify.adapters.in.rest.dto;

import java.util.List;

public record CallStageWebModel(
        int number,
        DateRangeWebModel duration,
        List<TranslatedTextWebModel> description
) {
}