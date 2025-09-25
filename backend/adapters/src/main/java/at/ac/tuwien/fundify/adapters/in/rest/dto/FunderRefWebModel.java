package at.ac.tuwien.fundify.adapters.in.rest.dto;


import at.ac.tuwien.fundify.domain.common.PublisherReference;
import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.EFundingTypeWebModel;

import java.util.List;

public record FunderRefWebModel(
        String id,
        String emailDomain,
        String risId,
        EFundingTypeWebModel type,
        List<TranslatedTextWebModel> name,
        String acronym,
        List<IdentifierWebModel> identifiers
) implements PublisherReference {
    public FunderRefWebModel {
        type = EFundingTypeWebModel.FUNDER;
    }

    @Override
    public String publisherId() {
        return id;
    }
}

