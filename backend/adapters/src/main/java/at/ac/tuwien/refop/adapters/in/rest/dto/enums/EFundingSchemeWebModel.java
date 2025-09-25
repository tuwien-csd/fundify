package at.ac.tuwien.refop.adapters.in.rest.dto.enums;


import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum EFundingSchemeWebModel {

    AWARD("Award"),
    GRANT("Grant"),
    RESEARCH_CONTRACT("Research Contract"),
    SCHOLARSHIP("Scholarship");

    private final String value;

    public static EFundingSchemeWebModel fromString(String value) {
        for (EFundingSchemeWebModel e : EFundingSchemeWebModel.values()) {
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
