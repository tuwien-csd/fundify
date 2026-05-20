package at.ac.tuwien.fundify.adapters.in.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AnnotateRequest(
    @NotBlank String callId,
    @NotBlank String universityId,
    @NotNull @Valid CallAnnotationWebModel annotation
) implements WebModel {

    @Override
    public String id() {
        return callId;
    }

    @Override
    public String publisherId() {
        return universityId;
    }
}