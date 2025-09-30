package at.ac.tuwien.fundify.adapters.in.rest.dto;

import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.ECurrencyWebModel;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;


public record MonetaryNumberWebModel(
        @DecimalMin("0")
        BigDecimal amount,
        ECurrencyWebModel currency
) {
}
