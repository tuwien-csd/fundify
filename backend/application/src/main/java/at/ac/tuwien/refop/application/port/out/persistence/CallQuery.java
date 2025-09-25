package at.ac.tuwien.refop.application.port.out.persistence;

import at.ac.tuwien.refop.domain.common.CallId;
import at.ac.tuwien.refop.domain.common.EPublicationStatus;
import at.ac.tuwien.refop.domain.common.ETargetGroup;
import at.ac.tuwien.refop.domain.common.FunderId;
import at.ac.tuwien.refop.domain.common.RisId;
import at.ac.tuwien.refop.domain.dto.CallDTO;
import at.ac.tuwien.refop.domain.funding.Call;
import at.ac.tuwien.refop.domain.funding.vo.enums.EAustrianState;
import at.ac.tuwien.refop.domain.funding.vo.enums.ECallType;
import at.ac.tuwien.refop.domain.funding.vo.enums.ERegionalScope;
import java.util.List;
import java.util.Optional;

public interface CallQuery {

    Optional<Call> find(RisId risId);

    Optional<Call> find(CallId callId);

    Optional<Call> find(RisId risId, EPublicationStatus status);

    List<Call> find(EPublicationStatus status);

    List<Call> findAll();

    List<Call> find(FunderId funderId);

    List<Call> find(FunderId funderId, EPublicationStatus status);

    List<Call> find(
            ECallType callType,
            ETargetGroup targetGroup,
            Boolean runningCalls,
            EAustrianState region,
            FunderId funderId,
            ERegionalScope applicantsScope,
            EPublicationStatus status
    );

    @Deprecated
    Optional<CallDTO> findDto(RisId risId, EPublicationStatus status);

    @Deprecated
    List<CallDTO> findDto(
            ECallType callType,
            ETargetGroup targetGroup,
            Boolean runningCalls,
            EAustrianState region,
            FunderId funderId,
            ERegionalScope applicantsScope,
            EPublicationStatus status
    );
}
