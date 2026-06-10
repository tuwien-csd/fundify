package at.ac.tuwien.fundify.application.service.calls;

import at.ac.tuwien.fundify.application.port.out.persistence.CallVersionRepository;
import at.ac.tuwien.fundify.domain.common.CallId;
import at.ac.tuwien.fundify.domain.funding.Call;
import at.ac.tuwien.fundify.domain.funding.CallUpdate;
import at.ac.tuwien.fundify.domain.funding.CallVersion;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EUpdateSource;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class CallVersioningService {

    private final CallVersionRepository callVersionRepository;

    public void createVersionIfChanged(Call existing, Call incoming, EUpdateSource source) {
        if (hasRelevantChange(existing, incoming)) {
            callVersionRepository.persist(snapshotOf(existing, source));
        }
    }

    public void createVersionIfChanged(Call existing, CallUpdate incoming, EUpdateSource source) {
        if (hasRelevantChange(existing, incoming)) {
            callVersionRepository.persist(snapshotOf(existing, source));
        }
    }

    public void deleteVersions(CallId callId) {
        callVersionRepository.deleteByCallId(callId);
    }

    private boolean hasRelevantChange(Call existing, Call incoming) {
        return anyChanged(
            existing.getName(), incoming.getName(),
            existing.getDescription(), incoming.getDescription(),
            existing.getEligibleApplicants(), incoming.getEligibleApplicants(),
            existing.getCallStages(), incoming.getCallStages(),
            existing.getCallVolumeAmount(), incoming.getCallVolumeAmount(),
            existing.getWebsite(), incoming.getWebsite()
        );
    }

    private boolean hasRelevantChange(Call existing, CallUpdate incoming) {
        return anyChanged(
            existing.getName(), incoming.getName(),
            existing.getDescription(), incoming.getDescription(),
            existing.getEligibleApplicants(), incoming.getEligibleApplicants(),
            existing.getCallStages(), incoming.getCallStages(),
            existing.getCallVolumeAmount(), incoming.getCallVolumeAmount(),
            existing.getWebsite(), incoming.getWebsite()
        );
    }

    private static boolean anyChanged(Object... pairs) {
        for (int i = 0; i < pairs.length; i += 2) {
            if (!Objects.equals(pairs[i], pairs[i + 1])) return true;
        }
        return false;
    }

    private CallVersion snapshotOf(Call call, EUpdateSource source) {
        return new CallVersion(
            null,
            call.getId(),
            LocalDateTime.now(),
            source,
            call.getName(),
            call.getDescription(),
            call.getEligibleApplicants(),
            call.getCallStages(),
            call.getCallVolumeAmount(),
            call.getWebsite()
        );
    }
}
