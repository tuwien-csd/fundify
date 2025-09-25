package at.ac.tuwien.refop.application.port.in.ris.api;

import at.ac.tuwien.refop.domain.common.ETargetGroup;
import at.ac.tuwien.refop.domain.common.FunderId;
import at.ac.tuwien.refop.domain.common.RisId;
import at.ac.tuwien.refop.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.refop.domain.dto.AnnotatedCallDTO;
import at.ac.tuwien.refop.domain.funding.vo.enums.EAustrianState;
import at.ac.tuwien.refop.domain.funding.vo.enums.ECallType;
import at.ac.tuwien.refop.domain.funding.vo.enums.ERegionalScope;
import java.util.List;

public interface PublishedAnnotatedCallAccessor {

    AnnotatedCallDTO getPublishedByUniversityRisIdAndCallRisId(RisId universityRisId, RisId callRisId)
        throws EntityNotFoundException;

    List<AnnotatedCallDTO> listByUniversityAndCallFilter(
            RisId universityRisId,
            ECallType callType,
            ETargetGroup targetGroup,
            Boolean runningCalls,
            EAustrianState region,
            FunderId funderId,
            ERegionalScope applicantsScope) throws EntityNotFoundException;
}
