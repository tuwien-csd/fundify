package at.ac.tuwien.fundify.adapters.in.rest.dto;

import lombok.NonNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record TicketCreateWebModel(
    @Schema(required = true)
    @NonNull
    String name,
    @Schema(required = true)
    @NonNull
    String subject,
    @Schema(required = true)
    @NonNull
    String category,
    @Schema(required = true)
    @NonNull
    String email,
    @Schema(required = true)
    @NonNull
    String message,
    @Schema(required = false)
    String kindOfInstitution
) {

}