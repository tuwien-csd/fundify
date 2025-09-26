package at.ac.tuwien.fundify.adapters.in.rest.dto;

import jakarta.validation.constraints.Min;

public record TimeSpanWebModel(
        @Min(0)
        Integer days,
        @Min(0)
        Integer months,
        @Min(0)
        Integer years
) {
}
