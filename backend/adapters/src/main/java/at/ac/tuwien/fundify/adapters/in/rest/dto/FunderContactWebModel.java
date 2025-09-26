package at.ac.tuwien.fundify.adapters.in.rest.dto;

import jakarta.validation.constraints.Email;

public record FunderContactWebModel(
        String name,
        @Email
        String email,
        String phone
) {}
