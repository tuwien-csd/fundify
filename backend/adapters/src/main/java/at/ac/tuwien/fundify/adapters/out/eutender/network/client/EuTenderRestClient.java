package at.ac.tuwien.fundify.adapters.out.eutender.network.client;

import at.ac.tuwien.fundify.adapters.common.eutender.mapper.EuTenderToRisMapper;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFunding;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFundingType;
import at.ac.tuwien.fundify.adapters.out.ris.network.client.GenericRisFundingRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class EuTenderRestClient implements GenericRisFundingRestClient {

    @RestClient
    EuTenderHttpRestClient httpClient;

    @Override
    public List<RisFunding> getFundings(RisFundingType fundingType) {
       return httpClient.get().getResults().stream()
                .map(EuTenderToRisMapper.INSTANCE::toRisCall)
                .toList();
    }

    @Override
    public RisFunding getFunding(String id) {
        throw new UnsupportedOperationException("EuTender does not support fetching a single funding by ID");
    }

    @Override
    public String getMemberId() {
        return "eutender";
    }
}
