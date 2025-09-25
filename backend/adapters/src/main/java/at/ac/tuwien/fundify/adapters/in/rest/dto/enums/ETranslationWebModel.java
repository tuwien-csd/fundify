package at.ac.tuwien.fundify.adapters.in.rest.dto.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ETranslationWebModel {
    ORIGINAL("o"),
    TRANSLATION("t");

    private final String value;

    public static ETranslationWebModel fromString(String value) {
        for (ETranslationWebModel e : ETranslationWebModel.values()) {
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

