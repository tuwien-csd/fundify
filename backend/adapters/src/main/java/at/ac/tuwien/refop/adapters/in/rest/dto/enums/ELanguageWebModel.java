package at.ac.tuwien.refop.adapters.in.rest.dto.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum ELanguageWebModel {
    ENGLISH("En"),
    GERMAN("De");

    private final String value;

    public static ELanguageWebModel fromString(String value) {
        for (ELanguageWebModel e : ELanguageWebModel.values()) {
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
