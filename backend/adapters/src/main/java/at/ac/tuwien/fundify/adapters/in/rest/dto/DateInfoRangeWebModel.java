package at.ac.tuwien.fundify.adapters.in.rest.dto;

import java.util.List;


public record DateInfoRangeWebModel(
        DateRangeWebModel range,
        List<TranslatedTextWebModel> info
) {
}


