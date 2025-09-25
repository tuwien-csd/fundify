package at.ac.tuwien.refop.adapters.in.rest.dto;

import java.time.LocalDateTime;


public record DateRangeWebModel(
        LocalDateTime start,
        LocalDateTime end
) {
}
