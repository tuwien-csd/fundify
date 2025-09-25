package at.ac.tuwien.refop.adapters.in.rest.dto.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum EAustrianStateWebModel {
    VIENNA("Vienna"),
    BURGENLAND("Burgenland"),
    LOWER_AUSTRIA("Lower Austria"),
    CARINTHIA("Carinthia"),
    UPPER_AUSTRIA("Upper Austria"),
    SALZBURG("Salzburg"),
    STYRIA("Styria"),
    TYROL("Tyrol"),
    VORARLBERG("Vorarlberg");

    private final String value;

    public static EAustrianStateWebModel fromString(String value) {
        for (EAustrianStateWebModel e : EAustrianStateWebModel.values()) {
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
