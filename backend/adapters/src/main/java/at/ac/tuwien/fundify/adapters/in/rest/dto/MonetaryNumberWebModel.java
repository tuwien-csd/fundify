package at.ac.tuwien.fundify.adapters.in.rest.dto;

import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.ECurrencyWebModel;

import java.math.BigDecimal;


public record MonetaryNumberWebModel(
        BigDecimal amount,
        ECurrencyWebModel currency
) {
}
