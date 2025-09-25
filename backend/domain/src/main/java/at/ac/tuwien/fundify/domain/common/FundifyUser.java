package at.ac.tuwien.fundify.domain.common;

public record FundifyUser (
    String id,
    String name,
    String affiliationId,
    String email
) {}