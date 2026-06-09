package at.ac.tuwien.fundify.application.port.out.persistence;

import at.ac.tuwien.fundify.domain.common.CallId;
import at.ac.tuwien.fundify.domain.funding.CallVersion;
import java.util.List;

public interface CallVersionRepository {

    void persist(CallVersion callVersion);

    List<CallVersion> findByCallId(CallId callId);
}
