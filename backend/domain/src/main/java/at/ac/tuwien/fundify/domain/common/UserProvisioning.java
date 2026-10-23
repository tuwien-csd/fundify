package at.ac.tuwien.fundify.domain.common;

import java.util.List;

/**
 * Everything needed to provision a Fundify account for someone who has never
 * logged in: the Keycloak profile fields plus the permissions an admin grants.
 */
public record UserProvisioning(
    String email,
    String firstName,
    String lastName,
    List<UserRole> roles,
    String affiliationId
) {

}
