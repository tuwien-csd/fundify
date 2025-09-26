package at.ac.tuwien.fundify.adapters.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record CallOwnerWebModel(
        @NotBlank
        String kind,
        @NotBlank
        String acronym,
        @NotBlank
        String id
){}
