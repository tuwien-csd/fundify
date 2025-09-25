package at.ac.tuwien.fundify.domain.funding;

import at.ac.tuwien.fundify.domain.common.FunderId;
import at.ac.tuwien.fundify.domain.common.PublisherReference;
import at.ac.tuwien.fundify.domain.common.RisId;
import at.ac.tuwien.fundify.domain.funding.vo.Identifier;
import at.ac.tuwien.fundify.domain.common.TranslatedText;

import java.util.List;

public record FunderReference (
        FunderId id,
        String emailDomain,
        RisId risId,
        List<TranslatedText> name,
        String website,
        String acronym,
        List<Identifier> identifiers
) implements PublisherReference {
    @Override
    public String publisherId() {
        return id.value();
    }
}