package at.ac.tuwien.fundify.adapters.common.eutender.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Metadata block of an EU Tender result.
 * All fields are string arrays (List&lt;String&gt;) as returned by the API,
 * even when they contain a single value or embedded JSON strings.
 * Fields referenced in the Feldermapping sheet are annotated with their
 * RIS Synergy mapping target.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EuTenderMetadata {

    /** → Funding.identifiers / Identifier.value  (e.g. "SC1-PHE-CORONAVIRUS-2020-2D") */
    private List<String> identifier;

    /** → Funding.name  (topic title) */
    private List<String> title;

    /** → Funding.acronym  (call identifier, e.g. "H2020-SC1-PHE-CORONAVIRUS-2020-2") */
    private List<String> callIdentifier;

    /** → Funding.description  (HTML content, needs sanitisation) */
    private List<String> descriptionByte;

    /**
     * → Funding.type, fundingScheme, characteristics, careerStages
     * Contains a JSON-encoded array of {@link EuTenderAction} objects.
     * Must be parsed with Jackson separately.
     */
    private List<String> actions;

    /** → Funding.subjects  (keyword strings) */
    private List<String> keywords;

    /** → Funding.targetGroups  (tags like "COVID-19") */
    private List<String> tags;

    /**
     * → Funding.amount, minProjectVolume, maxProjectVolume
     * Contains a JSON-encoded {@link EuTenderBudgetOverview} object.
     * Must be parsed with Jackson separately.
     */
    private List<String> budgetOverview;

    // ── Additional metadata fields ───────────────────────────────────────

    private List<String> latestInfos;
    private List<String> callTitle;
    private List<String> frameworkProgramme;
    private List<String> programmePeriod;
    private List<String> programmeDivision;
    private List<String> typesOfAction;
    private List<String> deadlineDate;
    private List<String> startDate;
    private List<String> deadlineModel;
    private List<String> status;
    private List<String> sortStatus;
    private List<String> language;
    private List<String> type;
    private List<String> focusArea;
    private List<String> crossCuttingPriorities;
    private List<String> workProgrammepart;
    private List<String> destinationDetails;
    private List<String> destinationDescription;
    private List<String> missionDetails;
    private List<String> missionDescription;
    private List<String> typeOfMGAs;
    private List<String> callccm2Id;
    private List<String> ccm2Id;
    private List<String> additionalInfos;
    private List<String> supportInfo;
    private List<String> topicConditions;
    private List<String> sepTemplate;
    private List<String> links;
    private List<String> url;
    private List<String> allowPartnerSearch;
    private List<String> callUpdates;
    private List<String> additionalDossiers;
    private List<String> infoPackDossiers;

    @JsonIgnoreProperties(ignoreUnknown = true)
    private List<String> es_SortDate;
    private List<String> es_ContentType;
    private List<String> es_Combine;
    private List<String> esST_URL;
    private List<String> esST_checksum;
    private List<String> esST_FileName;
    private List<String> esDA_QueueDate;
    private List<String> esDA_IngestDate;

    @com.fasterxml.jackson.annotation.JsonProperty("DATASOURCE")
    private List<String> datasource;

    @com.fasterxml.jackson.annotation.JsonProperty("REFERENCE")
    private List<String> reference;

}