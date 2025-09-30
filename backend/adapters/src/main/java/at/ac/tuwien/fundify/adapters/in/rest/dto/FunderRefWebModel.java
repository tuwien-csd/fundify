package at.ac.tuwien.fundify.adapters.in.rest.dto;


import at.ac.tuwien.fundify.domain.common.PublisherReference;
import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.EFundingTypeWebModel;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FunderRefWebModel(
        @NotNull
        String id,
        String emailDomain,
        String risId,
        EFundingTypeWebModel type,
        @NotNull
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

