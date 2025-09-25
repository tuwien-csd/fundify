package at.ac.tuwien.fundify.application.port.in.ris.api;

import at.ac.tuwien.fundify.domain.common.ETargetGroup;
import at.ac.tuwien.fundify.domain.common.FunderId;
import at.ac.tuwien.fundify.domain.common.RisId;
import at.ac.tuwien.fundify.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.fundify.domain.dto.CallDTO;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EAustrianState;
import at.ac.tuwien.fundify.domain.funding.vo.enums.ECallType;
import at.ac.tuwien.fundify.domain.funding.vo.enums.ERegionalScope;
import java.util.List;

public interface PublishedCallAccessor {

    CallDTO getByRisId(RisId risId) throws EntityNotFoundException;

    List<CallDTO> listFilteredBy(
            ECallType callType,
            ETargetGroup targetGroup,
            Boolean isRunningCall,
            EAustrianState state,
            FunderId funderId,
            ERegionalScope scope);
}
