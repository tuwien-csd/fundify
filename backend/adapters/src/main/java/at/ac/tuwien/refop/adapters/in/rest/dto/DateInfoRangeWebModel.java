package at.ac.tuwien.refop.adapters.in.rest.dto;

import java.util.List;


public record DateInfoRangeWebModel(
        DateRangeWebModel range,
        List<TranslatedTextWebModel> info
) {
}


