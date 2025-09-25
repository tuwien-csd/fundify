package at.ac.tuwien.fundify.adapters.in.rest.dto;

import java.time.LocalDateTime;


public record DateRangeWebModel(
        LocalDateTime start,
        LocalDateTime end
) {
}
