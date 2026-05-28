package at.ac.tuwien.fundify.adapters.common.eutender.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Parsed from the JSON string inside {@code metadata.actions[0]}.
 * Used to derive:
 *   - Funding.type / fundingScheme  (via status.abbreviation + deadlineDates presence)
 *   - Funding.characteristics       (via types[].typeOfAction)
 * Example JSON:
 * <pre>
 * {
 *   "status": { "id": 31094503, "abbreviation": "Closed", "description": "Closed" },
 *   "types": [{ "typeOfAction": "RIA Research and Innovation action", "typeOfMGA": [] }],
 *   "plannedOpeningDate": "19 May 2020",
 *   "submissionProcedure": { "id": 31094504, "abbreviation": "single-stage", "description": "single-stage" },
 *   "deadlineDates": ["11 June 2020"]
 * }
 * </pre>
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EuTenderAction {

    private ActionStatus status;
    private List<ActionType> types;
    private String plannedOpeningDate;
    private SubmissionProcedure submissionProcedure;
    private List<String> deadlineDates;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ActionStatus {
        private long id;
        private String abbreviation;
        private String description;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ActionType {
        private String typeOfAction;
        private List<String> typeOfMGA;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SubmissionProcedure {
        private long id;
        private String abbreviation;
        private String description;
    }
}
