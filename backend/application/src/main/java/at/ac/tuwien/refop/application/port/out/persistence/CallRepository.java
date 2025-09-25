package at.ac.tuwien.refop.application.port.out.persistence;

import at.ac.tuwien.refop.domain.common.*;
import at.ac.tuwien.refop.domain.dto.CallIdMapping;
import at.ac.tuwien.refop.domain.funding.Call;

import at.ac.tuwien.refop.domain.funding.CallUpdate;
import java.util.Optional;

public interface CallRepository {


    Call persist(Call call);

    Optional<Call> update(Call call);

    Optional<Call> update(CallUpdate callUpdate);

    boolean delete(CallId id);

    Optional<Call> findById(CallId id);

    Optional<Call> findByRisId(RisId risId);

    Optional<CallIdMapping> findIdMappingByRisId(RisId risId);
}
