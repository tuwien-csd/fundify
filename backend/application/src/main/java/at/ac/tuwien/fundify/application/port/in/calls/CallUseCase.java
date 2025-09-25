package at.ac.tuwien.fundify.application.port.in.calls;

import at.ac.tuwien.fundify.domain.common.CallId;
import at.ac.tuwien.fundify.domain.common.ESubscriptionStatus;
import at.ac.tuwien.fundify.domain.common.FundifyUser;
import at.ac.tuwien.fundify.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.fundify.domain.common.exceptions.FundifyException;
import at.ac.tuwien.fundify.domain.funding.Call;
import at.ac.tuwien.fundify.domain.funding.CallCreate;
import at.ac.tuwien.fundify.domain.funding.CallUpdate;
import lombok.NonNull;

public interface CallUseCase {

    // CRUD operations for Call
    CallId addCall(CallCreate call) throws FundifyException;

    Call getCall(CallId id) throws EntityNotFoundException;

    CallId updateCall(CallUpdate call) throws FundifyException;

    void deleteCall(CallId id) throws FundifyException;

    Call setSubscriptionForCurrentUser(@NonNull CallId id, @NonNull ESubscriptionStatus status, @NonNull FundifyUser user) throws FundifyException;

}
