package at.ac.tuwien.fundify.adapters.in.rest.dto;

import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.ECurrencyWebModel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;


public record MonetaryNumberWebModel(
        @NotNull @DecimalMin("0")
        BigDecimal amount,
        @NotNull
        ECurrencyWebModel currency
) {
}
