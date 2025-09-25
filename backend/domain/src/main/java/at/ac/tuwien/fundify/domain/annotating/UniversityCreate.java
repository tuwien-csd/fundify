package at.ac.tuwien.fundify.domain.annotating;

import at.ac.tuwien.fundify.domain.common.RisId;
import at.ac.tuwien.fundify.domain.common.TranslatedText;
import at.ac.tuwien.fundify.domain.common.PostAddress;
import java.util.List;
import lombok.NonNull;

public record UniversityCreate(
    @NonNull
    List<TranslatedText> name,
    @NonNull
    String website,
    // optional fields
    RisId risId,
    String emailDomain,
    String acronym,
    String submissionSystem,
    String phone,
    String crossRefDoi,
    PostAddress postAddress
) {

}