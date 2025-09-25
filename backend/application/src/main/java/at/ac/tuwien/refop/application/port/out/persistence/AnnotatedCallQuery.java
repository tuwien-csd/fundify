package at.ac.tuwien.refop.application.port.out.persistence;


import at.ac.tuwien.refop.domain.annotating.AnnotatedCallId;
import at.ac.tuwien.refop.domain.common.ETargetGroup;
import at.ac.tuwien.refop.domain.common.FunderId;
import at.ac.tuwien.refop.domain.dto.CallPreviewDTO;
import at.ac.tuwien.refop.domain.annotating.UniversityId;
import at.ac.tuwien.refop.domain.common.CallId;
import at.ac.tuwien.refop.domain.common.EPublicationStatus;
import at.ac.tuwien.refop.domain.dto.AnnotatedCallDTO;
import at.ac.tuwien.refop.domain.funding.vo.enums.EAustrianState;
import at.ac.tuwien.refop.domain.funding.vo.enums.ECallType;
import at.ac.tuwien.refop.domain.funding.vo.enums.ERegionalScope;

import java.util.List;
import java.util.Optional;

public interface AnnotatedCallQuery {

    Optional<AnnotatedCallDTO> find(UniversityId universityId, CallId callId, EPublicationStatus status);

    Optional<AnnotatedCallDTO> find(AnnotatedCallId id);

    Optional<AnnotatedCallDTO> find(CallId callId, UniversityId universityId);

    Optional<CallPreviewDTO> find(CallId callId);

    List<AnnotatedCallDTO> findAll();

    List<AnnotatedCallDTO> find(UniversityId universityId);

    List<AnnotatedCallDTO> find(
            UniversityId universityId,
            ECallType callType,
            ETargetGroup targetGroup,
            Boolean runningCalls,
            EAustrianState region,
            FunderId funderId,
            ERegionalScope applicantsScope,
            EPublicationStatus status
    );
}
