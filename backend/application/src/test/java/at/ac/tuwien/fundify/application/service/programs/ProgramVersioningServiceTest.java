package at.ac.tuwien.fundify.application.service.programs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import at.ac.tuwien.fundify.application.port.out.persistence.ProgramVersionRepository;
import at.ac.tuwien.fundify.domain.common.DateRange;
import at.ac.tuwien.fundify.domain.common.ELanguage;
import at.ac.tuwien.fundify.domain.common.EPublicationStatus;
import at.ac.tuwien.fundify.domain.common.ETranslation;
import at.ac.tuwien.fundify.domain.common.ProgramId;
import at.ac.tuwien.fundify.domain.common.TranslatedText;
import at.ac.tuwien.fundify.domain.funding.Program;
import at.ac.tuwien.fundify.domain.funding.ProgramVersion;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EUpdateSource;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProgramVersioningServiceTest {

    private static final ProgramId PROGRAM_ID = new ProgramId("program-1");
    private static final List<TranslatedText> DESCRIPTION =
        List.of(new TranslatedText("Program description", ELanguage.ENGLISH, ETranslation.ORIGINAL));
    private static final DateRange DURATION =
        new DateRange(LocalDateTime.of(2024, 1, 1, 0, 0), LocalDateTime.of(2026, 12, 31, 0, 0));

    @Mock
    private ProgramVersionRepository programVersionRepository;
    
    private ProgramVersioningService programVersioningService;

    @BeforeEach
    void setUp() {
        programVersioningService = new ProgramVersioningService(programVersionRepository);
    }
    
    @Test
    void createVersionIfChanged_noChange_persistNotCalled() {
        Program existing = buildProgram();
        Program incoming = buildProgram();

        programVersioningService.createVersionIfChanged(existing, incoming, EUpdateSource.MANUAL);

        verify(programVersionRepository, never()).persist(any());
    }

    @Test
    void createVersionIfChanged_descriptionChanged_persistCalledWithCorrectProgramIdAndSource() {
        Program existing = buildProgram();
        Program incoming = buildProgram();
        incoming.setDescription(List.of(new TranslatedText("Updated description", ELanguage.ENGLISH, ETranslation.ORIGINAL)));

        ArgumentCaptor<ProgramVersion> captor = ArgumentCaptor.forClass(ProgramVersion.class);

        programVersioningService.createVersionIfChanged(existing, incoming, EUpdateSource.MANUAL);

        verify(programVersionRepository).persist(captor.capture());
        ProgramVersion snapshot = captor.getValue();
        assertEquals(PROGRAM_ID, snapshot.getProgramId());
        assertEquals(EUpdateSource.MANUAL, snapshot.getUpdateSource());
    }

    @Test
    void createVersionIfChanged_durationChanged_persistCalled() {
        Program existing = buildProgram();
        Program incoming = buildProgram();
        incoming.setDuration(new DateRange(LocalDateTime.of(2025, 1, 1, 0, 0), LocalDateTime.of(2027, 12, 31, 0, 0)));

        programVersioningService.createVersionIfChanged(existing, incoming, EUpdateSource.SYNC);

        verify(programVersionRepository).persist(any(ProgramVersion.class));
    }

    @Test
    void createVersionIfChanged_irrelevantFieldChanged_persistNotCalled() {
        Program existing = buildProgram();
        Program incoming = buildProgram();
        incoming.setStatus(EPublicationStatus.PUBLISHED);

        programVersioningService.createVersionIfChanged(existing, incoming, EUpdateSource.MANUAL);

        verify(programVersionRepository, never()).persist(any());
    }

    private Program buildProgram() {
        Program program = new Program();
        program.setId(PROGRAM_ID);
        program.setDescription(DESCRIPTION);
        program.setDuration(DURATION);
        program.setStatus(EPublicationStatus.DRAFT);
        return program;
    }

}
