package at.ac.tuwien.fundify.adapters.in.rest.dto.enums;


import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum EFundingSchemeWebModel {

    AWARD("Award"),
    GRANT("Grant"),
    RESEARCH_CONTRACT("Research Contract"),
    SCHOLARSHIP("Scholarship"),
    SEMESTER_GRANT("Semester Grant"),
    SUMMER_GRANT("Summer Grant"),
    PRACTICAL_TRAINING("Practical Training"),
    SUBSIDY("Subsidy"),
    RESEARCH_ALLOWANCE("Research Allowance");

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
