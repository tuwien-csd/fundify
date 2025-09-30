package at.ac.tuwien.fundify.adapters.in.rest.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;


public record DateRangeWebModel(
        @NotNull
        LocalDateTime start,
        @NotNull
        LocalDateTime end
) {
}
