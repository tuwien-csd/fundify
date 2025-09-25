package at.ac.tuwien.fundify.adapters.in.rest.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record PostAddressWebModel(
    @Schema(required = true) String streetLine,
    @Schema(required = true) String city,
    @Schema(required = true) String postalCode,
    @Schema(required = true) String countryCode
) {
}

