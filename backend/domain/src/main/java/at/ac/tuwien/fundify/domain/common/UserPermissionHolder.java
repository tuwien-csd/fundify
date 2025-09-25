package at.ac.tuwien.fundify.domain.common;

import java.util.List;

public record UserPermissionHolder(
    String userId,
    List<UserRole> roles,
    String affiliationId
) {

}
