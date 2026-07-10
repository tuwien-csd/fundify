package at.ac.tuwien.fundify.domain.common;

public record KeycloakUser(
    String id,
    String username,
    String email,
    String firstName,
    String lastName,
    boolean enabled
) {}
