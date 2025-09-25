package at.ac.tuwien.refop.domain.common;

public record FundifyUser (
    String id,
    String name,
    String affiliationId,
    String email
) {}