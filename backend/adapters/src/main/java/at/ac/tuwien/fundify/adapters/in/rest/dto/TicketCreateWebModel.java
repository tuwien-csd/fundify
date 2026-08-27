package at.ac.tuwien.fundify.adapters.in.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record TicketCreateWebModel(
    @Schema(required = true)
    @NotBlank
    String firstName,
    @Schema(required = true)
    @NotBlank
    String lastName,
    @Schema(required = true)
    @NotBlank
    String subject,
    @Schema(required = true)
    @NotBlank
    String category,
    @Schema(required = true)
    @NotBlank
    @Email
    String email,
    @Schema(required = true)
    @NotBlank
    String message,
    @Schema(required = false)
    String kindOfInstitution
) {

}
