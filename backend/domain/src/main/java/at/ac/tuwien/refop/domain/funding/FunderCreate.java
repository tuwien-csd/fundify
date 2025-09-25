package at.ac.tuwien.refop.domain.funding;

import at.ac.tuwien.refop.domain.common.RisId;
import at.ac.tuwien.refop.domain.common.TranslatedText;
import at.ac.tuwien.refop.domain.funding.vo.Identifier;
import at.ac.tuwien.refop.domain.common.PostAddress;
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
    String acronym,
    List<Identifier> identifiers,
    String submissionSystem,
    String crossRefDoi,
    String phone,
    PostAddress postAddress,
    Boolean externallyAdministered
) {

}