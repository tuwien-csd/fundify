package at.ac.tuwien.fundify.domain.funding.vo;

import at.ac.tuwien.fundify.domain.funding.vo.enums.ECurrency;

import java.math.BigDecimal;

public record MonetaryNumber(
        BigDecimal amount,
        ECurrency currency
) {}
