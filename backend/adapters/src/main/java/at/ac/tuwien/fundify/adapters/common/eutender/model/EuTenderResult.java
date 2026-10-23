package at.ac.tuwien.fundify.adapters.common.eutender.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * A single result entry from the EU Tender Search API.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EuTenderResult {

    private String apiVersion;
    private String reference;

    /** URL to the topic detail JSON — maps to Funding.website */
    private String url;

    private String title;
    private String contentType;
    private String language;
    private String databaseLabel;
    private String database;
    private String summary;
    private double weight;
    private String groupById;
    private String content;
    private boolean accessRestriction;
    private String pages;
    private String checksum;

    /** The metadata object carrying all funding-relevant fields. */
    private EuTenderMetadata metadata;

    private Object enrichedMetadata;
    private List<Object> children;
    private List<Object> highlightedFragments;
    private String citation;
    private double score;

}