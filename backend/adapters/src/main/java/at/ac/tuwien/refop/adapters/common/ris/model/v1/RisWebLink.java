package at.ac.tuwien.refop.adapters.common.ris.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RisWebLink {

    @JsonProperty("url")
    private String url;

    @JsonProperty("description")
    private String description;
}