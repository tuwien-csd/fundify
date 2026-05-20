package at.ac.tuwien.fundify.adapters.in.rest.dto;

import at.ac.tuwien.fundify.domain.common.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record UserPermissionsUpdateWebModel(

    @Schema(required = true)
    @NotNull
    List<UserRole> roles,
    @Schema(required = true)
    @NotBlank
    String affiliationId) {

}
