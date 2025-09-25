package at.ac.tuwien.refop.adapters.out.ris.network.client;


import at.ac.tuwien.refop.adapters.common.ris.model.v1.RisFunding;
import at.ac.tuwien.refop.adapters.common.ris.model.v1.RisFundingType;
import io.quarkus.oidc.client.filter.OidcClientFilter;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;


@RegisterRestClient(configKey = "fwf")
@OidcClientFilter("fwf")
public interface FwfRestClient extends GenericRisFundingRestClient {

  @GET
  @Path("/funding/v1/fundings/")
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  List<RisFunding> getFundings(@QueryParam("fundingType") RisFundingType fundingType);

  @GET
  @Path("/funding/v1/fundings/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  RisFunding getFunding(@PathParam("id") String id);

  @Override
  default String getMemberId() {
    return "fwf";
  }
}

