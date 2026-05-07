package at.ac.tuwien.fundify.adapters.out.eutender.network.client;

import at.ac.tuwien.fundify.adapters.common.eutender.model.EuTenderResponse;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "eutender")
public interface EuTenderHttpRestClient {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    EuTenderResponse get();

}
