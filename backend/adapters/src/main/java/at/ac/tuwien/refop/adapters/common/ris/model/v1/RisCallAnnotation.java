package at.ac.tuwien.refop.adapters.common.ris.model.v1;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RisCallAnnotation {

    @JsonProperty("internalDeadline")
    private List<RisText> internalDeadline;

    @JsonProperty("keywords")
    private List<String> keywords;

    @JsonProperty("targetGroups")
    private List<String> targetGroups;

    @JsonProperty("links")
    private List<RisWebLink> links;

    @JsonProperty("contact")
    private RisUniversityContact contact;
}