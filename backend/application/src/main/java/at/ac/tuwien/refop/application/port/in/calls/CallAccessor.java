package at.ac.tuwien.refop.application.port.in.calls;

import at.ac.tuwien.refop.domain.common.CallId;
import at.ac.tuwien.refop.domain.common.EPublicationStatus;
import at.ac.tuwien.refop.domain.common.FunderId;
import at.ac.tuwien.refop.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.refop.domain.funding.Call;
import java.util.List;

public interface CallAccessor {

    Call getById(CallId id) throws EntityNotFoundException;

    List<Call> getAll();

    List<Call> getByStatus(EPublicationStatus status);

    List<Call> getByFunder(FunderId funderId);

    List<Call> getByFunderAndStatus(FunderId funderId, EPublicationStatus status);
}