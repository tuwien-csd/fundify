package at.ac.tuwien.refop.adapters.in.rest.dto;

import at.ac.tuwien.refop.adapters.in.rest.dto.enums.EFundingTypeWebModel;

import java.util.List;

public record ProgramRefWebModel(
        String id,
        String risId,
        EFundingTypeWebModel type,
        List<TranslatedTextWebModel> name,
        String acronym,
        List<IdentifierWebModel> identifiers
) {
    public ProgramRefWebModel {
        type = EFundingTypeWebModel.PROGRAM;
    }
}
