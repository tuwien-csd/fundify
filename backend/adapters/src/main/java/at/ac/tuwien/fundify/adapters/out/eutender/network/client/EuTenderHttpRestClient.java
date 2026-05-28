package at.ac.tuwien.fundify.adapters.out.eutender.network.client;

import at.ac.tuwien.fundify.adapters.common.eutender.model.EuTenderResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

@RegisterRestClient(configKey = "eutender")
public interface EuTenderHttpRestClient {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    EuTenderResponse search(@QueryParam("pageNumber") int pageNumber,
                            @QueryParam("pageSize") int pageSize,
                            @MultipartForm EuTenderRequest request);

}
