package at.ac.tuwien.fundify.adapters.common.eutender.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Top-level response from the EU Tender Search API.
 * GET <a href="https://api.tech.ec.europa.eu/search-api/prod/rest/search">...</a>
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EuTenderResponse {

    private String apiVersion;
    private String terms;
    private int responseTime;
    private int totalResults;
    private int pageNumber;
    private int pageSize;
    private String sort;
    private String groupByField;
    private QueryLanguage queryLanguage;
    private String spellingSuggestion;
    private List<Object> bestBets;
    private List<EuTenderResult> results;
    private List<Object> warnings;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QueryLanguage {
        private String language;
        private double probability;
    }
}