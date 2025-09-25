package at.ac.tuwien.refop.adapters.in.rest.dto.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum EDecisionProcessWebModel {
    PEER_REVIEW("Peer Review"),
    JURY("Jury"),
    BOARD_OF_TRUSTEES("Board of Trustees");

    private final String value;

    public static EDecisionProcessWebModel fromString(String value) {
        for (EDecisionProcessWebModel e : EDecisionProcessWebModel.values()) {
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
