package at.ac.tuwien.fundify.domain.funding;

import at.ac.tuwien.fundify.domain.common.RisId;
import at.ac.tuwien.fundify.domain.common.TranslatedText;
import at.ac.tuwien.fundify.domain.funding.vo.Identifier;
import at.ac.tuwien.fundify.domain.common.PostAddress;
import java.util.List;
import lombok.NonNull;


public record FunderCreate(

    // mandatory fields
    @NonNull
    List<TranslatedText> name,
    @NonNull
    String website,
    // optional fields
    RisId risId,
    String emailDomain,
    @NonNull
    String acronym,
    List<Identifier> identifiers,
    String submissionSystem,
    String crossRefDoi,
    String phone,
    PostAddress postAddress,
    Boolean externallyAdministered
) {

}