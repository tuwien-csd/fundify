package at.ac.tuwien.refop.domain.annotating;

import at.ac.tuwien.refop.domain.common.Entity;
import at.ac.tuwien.refop.domain.common.RisId;
import at.ac.tuwien.refop.domain.common.TranslatedText;
import at.ac.tuwien.refop.domain.common.PostAddress;
import at.ac.tuwien.refop.domain.common.CallOwner;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class University implements Entity<UniversityId>, CallOwner {
    private UniversityId id;
    private RisId risId;
    private List<TranslatedText> name;
    private String acronym;
    private String emailDomain;
    private String website;
    private String submissionSystem;
    private String phone;
    private PostAddress postAddress;

    @Override
    public String getOwnerId() {
        return id.value();
    }

    @Override
    public CallOwnerKind getCallOwnerKind() {
        return CallOwnerKind.UNIVERSITY;
    }

    @Override
    public String getAcronym() {
        return acronym;
    }
}