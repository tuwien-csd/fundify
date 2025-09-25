package at.ac.tuwien.refop.domain.funding.vo;

import at.ac.tuwien.refop.domain.funding.vo.enums.EIdentifierType;

public record Identifier(
        EIdentifierType type,
        String value
) {}