package at.ac.tuwien.fundify.adapters.in.rest.dto.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum EFundingCharacteristicWebModel {

    INTERNATIONAL_PROGRAMME("International Programme"),
    BILATERAL_PROGRAMME("Bilateral Programme"),
    NATIONAL_PROGRAMME("National Programme"),
    INDIVIDUAL_PROJECT("Individual Project"),
    CONSORTIUM("Consortium"),
    SCIENTIFIC_PROGRAMME("Scientific Programme"),
    COOPERATIVE_PROGRAMME("Cooperative Programme"),
    PERSONAL_GRANT("Personal Grant"),
    PROJECT_FUNDING("Project Funding"),
    INFRASTRUCTURE("Infrastructure"),
    NETWORKING("Networking"),
    MOBILITY_PROGRAMME("Mobility Programme");

    private final String value;

    public static EFundingCharacteristicWebModel fromString(String value) {
        for (EFundingCharacteristicWebModel e : EFundingCharacteristicWebModel.values()) {
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
