package at.ac.tuwien.fundify.application.service.calls;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import at.ac.tuwien.fundify.application.port.out.persistence.CallVersionRepository;
import at.ac.tuwien.fundify.domain.common.CallId;
import at.ac.tuwien.fundify.domain.common.ELanguage;
import at.ac.tuwien.fundify.domain.common.EPublicationStatus;
import at.ac.tuwien.fundify.domain.common.ETranslation;
import at.ac.tuwien.fundify.domain.common.TranslatedText;
import at.ac.tuwien.fundify.domain.funding.Call;
import at.ac.tuwien.fundify.domain.funding.CallUpdate;
import at.ac.tuwien.fundify.domain.funding.CallVersion;
import at.ac.tuwien.fundify.domain.funding.vo.CallStage;
import at.ac.tuwien.fundify.domain.funding.vo.MonetaryNumber;
import at.ac.tuwien.fundify.domain.funding.vo.enums.ECurrency;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EUpdateSource;
import at.ac.tuwien.fundify.domain.common.DateRange;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CallVersioningServiceTest {

    private static final CallId CALL_ID = new CallId("call-1");
    private static final List<TranslatedText> NAME =
        List.of(new TranslatedText("Funding Call", ELanguage.ENGLISH, ETranslation.ORIGINAL));
    private static final List<TranslatedText> DESCRIPTION =
        List.of(new TranslatedText("A description", ELanguage.ENGLISH, ETranslation.ORIGINAL));
    private static final List<TranslatedText> ELIGIBLE_APPLICANTS =
        List.of(new TranslatedText("Universities", ELanguage.ENGLISH, ETranslation.ORIGINAL));
    private static final List<CallStage> CALL_STAGES = List.of(
        new CallStage(1, new DateRange(LocalDateTime.of(2024, 1, 1, 0, 0), LocalDateTime.of(2024, 6, 1, 0, 0)), List.of())
    );
    private static final MonetaryNumber VOLUME = new MonetaryNumber(new BigDecimal("1000000"), ECurrency.EUR);
    private static final List<String> WEBSITE = List.of("https://example.com");

    @Mock
    private CallVersionRepository callVersionRepository;

    private CallVersioningService callVersioningService;

    @BeforeEach
    void setUp() {
        callVersioningService = new CallVersioningService(callVersionRepository);
    }

    @Test
    void createVersionIfChanged_noChange_persistNotCalled() {
        Call existing = buildCall();
        Call incoming = buildCall();
        
        callVersioningService.createVersionIfChanged(existing, incoming, EUpdateSource.MANUAL);

        verify(callVersionRepository, never()).persist(any());
    }

    @Test
    void createVersionIfChanged_nameChanged_persistCalledWithCorrectCallIdAndSource() {
        Call existing = buildCall();
        Call incoming = buildCall();
        incoming.setName(List.of(new TranslatedText("Updated Name", ELanguage.ENGLISH, ETranslation.ORIGINAL)));

        ArgumentCaptor<CallVersion> captor = ArgumentCaptor.forClass(CallVersion.class);

        callVersioningService.createVersionIfChanged(existing, incoming, EUpdateSource.MANUAL);
        
        verify(callVersionRepository).persist(captor.capture());
        CallVersion snapshot = captor.getValue();
        assertEquals(CALL_ID, snapshot.getCallId());
        assertEquals(EUpdateSource.MANUAL, snapshot.getUpdateSource());
    }

    @Test
    void createVersionIfChanged_descriptionChanged_persistCalled() {
        Call existing = buildCall();
        Call incoming = buildCall();
        incoming.setDescription(List.of(new TranslatedText("New description", ELanguage.ENGLISH, ETranslation.ORIGINAL)));
        
        callVersioningService.createVersionIfChanged(existing, incoming, EUpdateSource.SYNC);

        verify(callVersionRepository).persist(any(CallVersion.class));
    }

    @Test
    void createVersionIfChanged_irrelevantFieldChanged_persistNotCalled() {
        Call existing = buildCall();
        Call incoming = buildCall();
        incoming.setStatus(EPublicationStatus.PUBLISHED);
        
        callVersioningService.createVersionIfChanged(existing, incoming, EUpdateSource.MANUAL);
        
        verify(callVersionRepository, never()).persist(any());
    }

    @Test
    void createVersionIfChangedCallUpdate_noChange_persistNotCalled() {
        Call existing = buildCall();
        CallUpdate incoming = buildCallUpdate();

        callVersioningService.createVersionIfChanged(existing, incoming, EUpdateSource.SYNC);

        verify(callVersionRepository, never()).persist(any());
    }

    @Test
    void createVersionIfChangedCallUpdate_nameChanged_persistCalledWithCorrectCallIdAndSource() {
        Call existing = buildCall();
        CallUpdate incoming = buildCallUpdate();
        incoming.setName(List.of(new TranslatedText("Updated Name", ELanguage.ENGLISH, ETranslation.ORIGINAL)));

        ArgumentCaptor<CallVersion> captor = ArgumentCaptor.forClass(CallVersion.class);

        callVersioningService.createVersionIfChanged(existing, incoming, EUpdateSource.SYNC);

        verify(callVersionRepository).persist(captor.capture());
        CallVersion snapshot = captor.getValue();
        assertEquals(CALL_ID, snapshot.getCallId());
        assertEquals(EUpdateSource.SYNC, snapshot.getUpdateSource());
    }

    private Call buildCall() {
        Call call = new Call();
        call.setId(CALL_ID);
        call.setName(NAME);
        call.setDescription(DESCRIPTION);
        call.setEligibleApplicants(ELIGIBLE_APPLICANTS);
        call.setCallStages(CALL_STAGES);
        call.setCallVolumeAmount(VOLUME);
        call.setWebsite(WEBSITE);
        call.setStatus(EPublicationStatus.DRAFT);
        return call;
    }

    private CallUpdate buildCallUpdate() {
        CallUpdate update = new CallUpdate();
        update.setId(CALL_ID);
        update.setName(NAME);
        update.setDescription(DESCRIPTION);
        update.setEligibleApplicants(ELIGIBLE_APPLICANTS);
        update.setCallStages(CALL_STAGES);
        update.setCallVolumeAmount(VOLUME);
        update.setWebsite(WEBSITE);
        update.setStatus(EPublicationStatus.DRAFT);
        return update;
    }
}
