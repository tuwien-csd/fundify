package at.ac.tuwien.fundify.adapters.in.rest.dto;


import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.EIdentifierTypeWebModel;
import io.smallrye.common.constraint.NotNull;
import jakarta.validation.constraints.NotBlank;

public record IdentifierWebModel(
        @NotNull
        EIdentifierTypeWebModel type,
        @NotBlank
        String value
) {
}