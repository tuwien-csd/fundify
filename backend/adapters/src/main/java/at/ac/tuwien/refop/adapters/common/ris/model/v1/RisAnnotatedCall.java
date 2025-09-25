package at.ac.tuwien.refop.adapters.common.ris.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RisAnnotatedCall {

    @JsonProperty("call")
    private RisCall call = null;

    @JsonProperty("annotation")
    private RisCallAnnotation annotation = null;
}