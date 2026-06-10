package at.ac.tuwien.fundify.application.service.programs;

import at.ac.tuwien.fundify.application.port.out.persistence.ProgramVersionRepository;
import at.ac.tuwien.fundify.domain.funding.Program;
import at.ac.tuwien.fundify.domain.funding.ProgramVersion;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EUpdateSource;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class ProgramVersioningService {

    private final ProgramVersionRepository programVersionRepository;

    public void createVersionIfChanged(Program existing, Program incoming, EUpdateSource source) {
        if (hasRelevantChange(existing, incoming)) {
            programVersionRepository.persist(snapshotOf(existing, source));
        }
    }

    private boolean hasRelevantChange(Program existing, Program incoming) {
        return anyChanged(
            existing.getDescription(), incoming.getDescription(),
            existing.getDuration(), incoming.getDuration()
        );
    }

    private static boolean anyChanged(Object... pairs) {
        for (int i = 0; i < pairs.length; i += 2) {
            if (!Objects.equals(pairs[i], pairs[i + 1])) return true;
        }
        return false;
    }

    private ProgramVersion snapshotOf(Program program, EUpdateSource source) {
        return new ProgramVersion(
            null,
            program.getId(),
            LocalDateTime.now(),
            source,
            program.getDescription(),
            program.getDuration()
        );
    }
}
