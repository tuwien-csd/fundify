package at.ac.tuwien.fundify.adapters.in.rest.dto;


import org.eclipse.microprofile.openapi.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
public record StandardizedSubjectWebModel(
        int level,
        @Schema(required = true) @NotBlank
        String code,
        @Schema(required = true) @NotBlank
        String title

) {
}
