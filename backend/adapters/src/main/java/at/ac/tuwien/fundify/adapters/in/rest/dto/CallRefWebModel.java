package at.ac.tuwien.fundify.adapters.in.rest.dto;


import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.EFundingTypeWebModel;

import java.util.List;

public record CallRefWebModel(
        String id,
        String risId,
        EFundingTypeWebModel type,
        List<TranslatedTextWebModel> name,
        String acronym,
        List<IdentifierWebModel> identifiers
) {
    public CallRefWebModel {
        type = EFundingTypeWebModel.CALL;
    }
}