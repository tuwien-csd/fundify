package at.ac.tuwien.fundify.adapters.common.eutender.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * Parsed from the JSON string inside {@code metadata.budgetOverview[0]}.
 * Used to derive:
 *   - Funding.amount           (sum of all budgetYearMap values for the matching topic)
 *   - Funding.minProjectVolume (maxcontribution if present)
 *   - Funding.maxProjectVolume (maxcontribution if present)
 * Example JSON:
 * <pre>
 * {
 *   "budgetTopicActionMap": {
 *     "3304058": [{
 *       "action": "SC1-PHE-CORONAVIRUS-2020-2D - RIA Research and Innovation action",
 *       "plannedOpeningDate": "19 May 2020",
 *       "deadlineModel": "single-stage",
 *       "deadlineDates": ["11 June 2020"],
 *       "budgetYearMap": { "2020": 20000000 },
 *       "budgetTopicActionMap": {}
 *     }]
 *   },
 *   "budgetYearsColumns": ["2020"]
 * }
 * </pre>
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EuTenderBudgetOverview {

    /**
     * Keys are internal topic-action IDs.
     * Values are lists of {@link BudgetTopicAction} entries.
     */
    private Map<String, List<BudgetTopicAction>> budgetTopicActionMap;

    private List<String> budgetYearsColumns;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BudgetTopicAction {

        private String action;
        private String plannedOpeningDate;
        private String deadlineModel;
        private List<String> deadlineDates;
        private Long minContribution;
        private Long maxContribution;

        /** Year → budget amount in EUR (e.g. {"2020": 20000000}) */
        private Map<String, Long> budgetYearMap;

        /** Nested sub-actions (usually empty). */
        private Map<String, List<BudgetTopicAction>> budgetTopicActionMap;
    }
}
