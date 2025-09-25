package at.ac.tuwien.refop.adapters.in.rest.dto;


import at.ac.tuwien.refop.adapters.in.rest.dto.enums.EIdentifierTypeWebModel;

public record IdentifierWebModel(
        EIdentifierTypeWebModel type,
        String value
) {
}