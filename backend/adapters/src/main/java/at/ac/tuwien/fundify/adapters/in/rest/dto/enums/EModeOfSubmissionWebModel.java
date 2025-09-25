package at.ac.tuwien.fundify.adapters.in.rest.dto.enums;


import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum EModeOfSubmissionWebModel {

    ONLINE_FULL("online (full)"),
    ONLINE_PARTLY("online (partly)"),
    OFFLINE("offline");

    private final String label;

    public static EModeOfSubmissionWebModel fromString(String label) {
        for (EModeOfSubmissionWebModel e : EModeOfSubmissionWebModel.values()) {
            if (e.label.equals(label)) {
                return e;
            }
        }
        return null;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }
}
