package at.ac.tuwien.fundify.domain.common;


import lombok.NonNull;

public record PostAddress(
    @NonNull
    String streetLine,
    @NonNull
    String city,
    @NonNull
    String postalCode,
    @NonNull
    String countryCode
) {}
