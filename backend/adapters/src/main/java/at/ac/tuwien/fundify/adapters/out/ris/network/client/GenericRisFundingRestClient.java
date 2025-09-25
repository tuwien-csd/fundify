package at.ac.tuwien.fundify.adapters.out.ris.network.client;

import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFunding;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFundingType;
import java.util.List;

public interface GenericRisFundingRestClient {

    List<RisFunding> getFundings(RisFundingType fundingType);

    RisFunding getFunding(String id);

    default String getMemberId() {
      throw new UnsupportedOperationException("If you extend this interface, you need to override this method!");
    }
}
