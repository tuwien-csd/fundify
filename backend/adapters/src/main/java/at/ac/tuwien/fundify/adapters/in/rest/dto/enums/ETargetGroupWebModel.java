package at.ac.tuwien.fundify.adapters.in.rest.dto.enums;

import
        com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ETargetGroupWebModel {

    UNIVERSITY("University"),
    UNIVERSITY_OF_APPLIED_SCIENCES("University of Applied Sciences"),
    PRIVATE_UNIVERSITY("Private University"),
    RESEARCH_INSTITUTE("Research Institute"),
    COMPANY("Company"),
    PRIVATE_NON_PROFIT("Private Non-Profit"),
    INDEPENDENT_RESEARCHER("Independent Researcher"),
    GOVERNMENT("Government");

    private final String value;

    public static ETargetGroupWebModel fromString(String value) {
        for (ETargetGroupWebModel e : ETargetGroupWebModel.values()) {
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
