package at.ac.tuwien.fundify.adapters.in.rest.dto;

import at.ac.tuwien.fundify.domain.common.UserRole;
import java.util.List;
import lombok.NonNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record UserPermissionsUpdateWebModel(

    @Schema(required = true)
    @NonNull
    List<UserRole> roles,
    @Schema(required = true)
    @NonNull
    String affiliationId) {

}
