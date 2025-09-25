package at.ac.tuwien.fundify.adapters.in.rest.dto.enums;


import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ERegionalScopeWebModel {
    NATIONAL("National"),
    REGIONAL("Regional");

    private final String value;

    public static ERegionalScopeWebModel fromString(String value) {
        for (ERegionalScopeWebModel e : ERegionalScopeWebModel.values()) {
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
