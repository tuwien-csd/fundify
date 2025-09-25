package at.ac.tuwien.refop.application.service.ris.api;

import at.ac.tuwien.refop.application.port.in.ris.api.PublishedCallAccessor;
import at.ac.tuwien.refop.application.port.out.persistence.CallQuery;
import at.ac.tuwien.refop.domain.common.EPublicationStatus;
import at.ac.tuwien.refop.domain.common.ETargetGroup;
import at.ac.tuwien.refop.domain.common.FunderId;
import at.ac.tuwien.refop.domain.common.RisId;
import at.ac.tuwien.refop.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.refop.domain.dto.CallDTO;
import at.ac.tuwien.refop.domain.funding.vo.enums.EAustrianState;
import at.ac.tuwien.refop.domain.funding.vo.enums.ECallType;
import at.ac.tuwien.refop.domain.funding.vo.enums.ERegionalScope;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class PublishedCallAcessorImpl implements PublishedCallAccessor {

    private static final EPublicationStatus PUBLISHED = EPublicationStatus.PUBLISHED;

    private final CallQuery callQuery;

    @Override
    public CallDTO getByRisId(RisId risId) throws EntityNotFoundException {
        Optional<CallDTO> call = callQuery.findDto(risId, PUBLISHED);
        return call.orElseThrow(() -> EntityNotFoundException.callNotFound(risId.toString()));
    }

    @Override
    public List<CallDTO> listFilteredBy(
            ECallType callType,
            ETargetGroup targetGroup,
            Boolean isRunningCall,
            EAustrianState state,
            FunderId funderId,
            ERegionalScope scope) {
        return callQuery.findDto(callType, targetGroup, isRunningCall, state, funderId, scope, PUBLISHED);
    }
}
