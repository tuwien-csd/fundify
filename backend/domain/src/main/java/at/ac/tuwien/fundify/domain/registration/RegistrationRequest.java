package at.ac.tuwien.fundify.domain.registration;

import java.time.Instant;

/**
 * A pending request from someone who wants an account in Fundify, captured from
 * the public contact form (category "registration"). The record exists only
 * while the request is pending: an admin either approves it (which provisions a
 * Keycloak user and then deletes the request) or rejects it (which deletes the
 * request). There is therefore no status to track.
 */
public record RegistrationRequest(
    String id,
    String name,
    String email,
    String kindOfInstitution,
    String message,
    Instant createdAt
) {

}
