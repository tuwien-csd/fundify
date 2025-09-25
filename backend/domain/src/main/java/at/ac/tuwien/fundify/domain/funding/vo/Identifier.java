package at.ac.tuwien.fundify.domain.funding.vo;

import at.ac.tuwien.fundify.domain.funding.vo.enums.EIdentifierType;

public record Identifier(
        EIdentifierType type,
        String value
) {}