package at.ac.tuwien.fundify.adapters.in.rest.dto.enums;


import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ELegalTypeWebModel {

    PROJECT26("§26 Project"),
    PROJECT27("§27 Project");

    private final String value;

    public static ELegalTypeWebModel fromString(String value) {
        for (ELegalTypeWebModel e : ELegalTypeWebModel.values()) {
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
