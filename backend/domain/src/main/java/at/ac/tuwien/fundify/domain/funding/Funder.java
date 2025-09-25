package at.ac.tuwien.fundify.domain.funding;

import at.ac.tuwien.fundify.domain.common.Entity;
import at.ac.tuwien.fundify.domain.common.FunderId;
import at.ac.tuwien.fundify.domain.common.RisId;
import at.ac.tuwien.fundify.domain.common.TranslatedText;
import at.ac.tuwien.fundify.domain.funding.vo.Identifier;
import at.ac.tuwien.fundify.domain.common.PostAddress;
import at.ac.tuwien.fundify.domain.common.CallOwner;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Funder implements Entity<FunderId>, CallOwner {

  // mandatory fields
  @NonNull
  private FunderId id;
  @NonNull
  private List<TranslatedText> name;
  private RisId risId; //TODO: Mark as NonNull
  @NonNull
  private String website;
  // optional fields
  private String emailDomain; // e.g format: fwf.ac.at
  private String acronym;
  private List<Identifier> identifiers;
  private String submissionSystem;
  private String phone;
  private String crossRefDoi;
  private PostAddress postAddress;
  private Boolean externallyAdministered;

    @Override
    public String getOwnerId() {
        return id.value();
    }

    @Override
    public CallOwnerKind getCallOwnerKind() {
        return CallOwnerKind.FUNDER;
    }
}