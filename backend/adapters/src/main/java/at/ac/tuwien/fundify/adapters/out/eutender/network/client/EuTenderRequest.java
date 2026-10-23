package at.ac.tuwien.fundify.adapters.out.eutender.network.client;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class EuTenderRequest {

    @FormParam("query")
    @PartType(MediaType.APPLICATION_JSON)
    //Query to NOT fetch closed calls
    public String query = "{\"bool\":{\"must\":[{\"terms\":{\"type\":[\"1\",\"2\",\"8\"]}},{\"terms\":{\"status\":[\"31094502\",\"31094501\"]}}]}}";

    @FormParam("languages")
    @PartType(MediaType.APPLICATION_JSON)
    public String languages = "[\"en\"]";

    @FormParam("sort")
    @PartType(MediaType.APPLICATION_JSON)
    public String sort = "{\"order\":\"DESC\",\"field\":\"startDate\"}";

}
