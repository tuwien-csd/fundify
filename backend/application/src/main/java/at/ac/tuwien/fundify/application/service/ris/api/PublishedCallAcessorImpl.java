package at.ac.tuwien.fundify.application.service.ris.api;

import at.ac.tuwien.fundify.application.port.in.ris.api.PublishedCallAccessor;
import at.ac.tuwien.fundify.application.port.out.persistence.CallQuery;
import at.ac.tuwien.fundify.domain.common.EPublicationStatus;
import at.ac.tuwien.fundify.domain.common.ETargetGroup;
import at.ac.tuwien.fundify.domain.common.FunderId;
import at.ac.tuwien.fundify.domain.common.RisId;
import at.ac.tuwien.fundify.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.fundify.domain.dto.CallDTO;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EAustrianState;
import at.ac.tuwien.fundify.domain.funding.vo.enums.ECallType;
import at.ac.tuwien.fundify.domain.funding.vo.enums.ERegionalScope;
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
