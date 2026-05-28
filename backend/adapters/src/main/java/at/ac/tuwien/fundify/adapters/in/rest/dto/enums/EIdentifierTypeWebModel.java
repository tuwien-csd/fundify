package at.ac.tuwien.fundify.adapters.in.rest.dto.enums;


import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum EIdentifierTypeWebModel {
    CROSSREF_GRANTID("Crossref Grant ID"),
    PROJECT_NUMBER("Project Number"),
    APPLICATION_NUMBER("Application Number"),
    ORCID("ORCID"),
    ROR("ROR"),
    RINGGOLD("Ringgold"),
    RIS_SYNERGY("Ris Synergy"),
    EU_ID("EU-ID"),
    CROSSREF_FUNDERID("CROSSREF_FUNDERID");

    private final String value;

    public static EIdentifierTypeWebModel fromString(String value) {
        for (EIdentifierTypeWebModel e : EIdentifierTypeWebModel.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
