package at.ac.tuwien.refop.adapters.in.rest.dto.enums;

import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonValue;

@AllArgsConstructor
public enum EAnswerYNWebModel {

    YES("Yes"),
    NO("No");

    private final String value;

    public static EAnswerYNWebModel fromString(String value) {
        for (EAnswerYNWebModel e : EAnswerYNWebModel.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        return NO;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
