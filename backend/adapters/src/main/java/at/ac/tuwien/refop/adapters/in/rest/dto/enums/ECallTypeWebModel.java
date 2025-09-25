package at.ac.tuwien.refop.adapters.in.rest.dto.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum ECallTypeWebModel {
    CALL("Call"),
    ONGOING_CALL("Ongoing Call");

    private final String value;

    public static ECallTypeWebModel fromString(String value) {
        for (ECallTypeWebModel e : ECallTypeWebModel.values()) {
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
