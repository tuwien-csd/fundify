package at.ac.tuwien.fundify.adapters.out.ris.network;

import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFunding;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFundingType;
import at.ac.tuwien.fundify.adapters.out.ris.network.client.GenericRisFundingRestClient;
import at.ac.tuwien.fundify.adapters.out.ris.network.config.RisClientConfiguration;
import at.ac.tuwien.fundify.application.port.out.notification.SyncErrorNotificationService;
import at.ac.tuwien.fundify.application.port.out.ticketing.TicketingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.ProcessingException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RisSynergyNetworkAdapterTest {

    private static final String MEMBER_ID = "fwf";
    private static final String CONTACT_EMAIL = "contact@fwf.ac.at";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<RisFunding>> FUNDING_LIST_TYPE = new TypeReference<>() {};

    @Mock
    private TicketingService ticketingService;
    @Mock
    private RisClientConfiguration config;
    @Mock
    private SyncErrorNotificationService notificationService;

    @Test
    void fetchPrograms_sendsNotificationWhenContactEmailIsConfigured() {
        RisSynergyNetworkAdapter adapter = buildAdapter(Map.of(MEMBER_ID, CONTACT_EMAIL));
        GenericRisFundingRestClient client = clientThrowing(RisFundingType.PROGRAMME, plainProcessingException());

        assertThrows(ProcessingException.class, () -> adapter.fetchPrograms(client, MEMBER_ID));

        verify(notificationService).sendSyncErrorNotification(eq(MEMBER_ID), eq(CONTACT_EMAIL), anyList());
    }

    @Test
    void fetchPrograms_skipsNotificationWhenNoContactEmailConfigured() {
        RisSynergyNetworkAdapter adapter = buildAdapter(Collections.emptyMap());
        GenericRisFundingRestClient client = clientThrowing(RisFundingType.PROGRAMME, plainProcessingException());

        assertThrows(ProcessingException.class, () -> adapter.fetchPrograms(client, MEMBER_ID));

        verifyNoInteractions(notificationService);
    }

    @Test
    void fetchPrograms_alwaysCreatesTicketOnProcessingException() {
        RisSynergyNetworkAdapter adapter = buildAdapter(Map.of(MEMBER_ID, CONTACT_EMAIL));
        GenericRisFundingRestClient client = clientThrowing(RisFundingType.PROGRAMME, plainProcessingException());

        assertThrows(ProcessingException.class, () -> adapter.fetchPrograms(client, MEMBER_ID));

        verify(ticketingService).createOrAppend(
            argThat(ticket -> ticket.ticketKey().contains(MEMBER_ID.toUpperCase()))
        );
    }

    @Test
    void fetchPrograms_notificationContainsUnknownTypeId_whenJsonHasUnknownTypeDiscriminator() {
        // Simulates a provider returning a funding type not defined in our model
        ProcessingException pe = parseFailure("[{\"type\": \"GRANT\", \"id\": \"abc123\"}]");

        RisSynergyNetworkAdapter adapter = buildAdapter(Map.of(MEMBER_ID, CONTACT_EMAIL));
        GenericRisFundingRestClient client = clientThrowing(RisFundingType.PROGRAMME, pe);

        assertThrows(ProcessingException.class, () -> adapter.fetchPrograms(client, MEMBER_ID));

        assertThat(captureNotificationTexts())
            .anyMatch(t -> t.contains("GRANT"));
    }

    @Test
    void fetchCalls_sendsNotificationWhenContactEmailIsConfigured() {
        RisSynergyNetworkAdapter adapter = buildAdapter(Map.of(MEMBER_ID, CONTACT_EMAIL));
        GenericRisFundingRestClient client = clientThrowing(RisFundingType.CALL, plainProcessingException());

        assertThrows(ProcessingException.class, () -> adapter.fetchCalls(client, MEMBER_ID));

        verify(notificationService).sendSyncErrorNotification(eq(MEMBER_ID), eq(CONTACT_EMAIL), anyList());
    }

    @Test
    void fetchCalls_skipsNotificationWhenNoContactEmailConfigured() {
        RisSynergyNetworkAdapter adapter = buildAdapter(Collections.emptyMap());
        GenericRisFundingRestClient client = clientThrowing(RisFundingType.CALL, plainProcessingException());

        assertThrows(ProcessingException.class, () -> adapter.fetchCalls(client, MEMBER_ID));

        verifyNoInteractions(notificationService);
    }

    @Test
    void fetchCalls_alwaysCreatesTicketOnProcessingException() {
        RisSynergyNetworkAdapter adapter = buildAdapter(Map.of(MEMBER_ID, CONTACT_EMAIL));
        GenericRisFundingRestClient client = clientThrowing(RisFundingType.CALL, plainProcessingException());

        assertThrows(ProcessingException.class, () -> adapter.fetchCalls(client, MEMBER_ID));

        verify(ticketingService).createOrAppend(
            argThat(ticket -> ticket.ticketKey().contains(MEMBER_ID.toUpperCase()))
        );
    }

    @Test
    void fetchCalls_notificationContainsUnknownTypeId_whenJsonHasUnknownTypeDiscriminator() {
        ProcessingException pe = parseFailure("[{\"type\": \"GRANT\", \"id\": \"xyz789\"}]");

        RisSynergyNetworkAdapter adapter = buildAdapter(Map.of(MEMBER_ID, CONTACT_EMAIL));
        GenericRisFundingRestClient client = clientThrowing(RisFundingType.CALL, pe);

        assertThrows(ProcessingException.class, () -> adapter.fetchCalls(client, MEMBER_ID));

        assertThat(captureNotificationTexts())
            .anyMatch(t -> t.contains("GRANT"));
    }

    @Test
    void fetchOnGoingCalls_sendsNotificationWhenContactEmailIsConfigured() {
        RisSynergyNetworkAdapter adapter = buildAdapter(Map.of(MEMBER_ID, CONTACT_EMAIL));
        GenericRisFundingRestClient client = clientThrowing(RisFundingType.ONGOING_CALL, plainProcessingException());

        assertThrows(ProcessingException.class, () -> adapter.fetchOnGoingCalls(client, MEMBER_ID));

        verify(notificationService).sendSyncErrorNotification(eq(MEMBER_ID), eq(CONTACT_EMAIL), anyList());
    }

    @Test
    void fetchOnGoingCalls_skipsNotificationWhenNoContactEmailConfigured() {
        RisSynergyNetworkAdapter adapter = buildAdapter(Collections.emptyMap());
        GenericRisFundingRestClient client = clientThrowing(RisFundingType.ONGOING_CALL, plainProcessingException());

        assertThrows(ProcessingException.class, () -> adapter.fetchOnGoingCalls(client, MEMBER_ID));

        verifyNoInteractions(notificationService);
    }

    @Test
    void fetchOnGoingCalls_notificationContainsUnknownTypeId_whenJsonHasUnknownTypeDiscriminator() {
        ProcessingException pe = parseFailure("[{\"type\": \"GRANT\", \"id\": \"zzz\"}]");

        RisSynergyNetworkAdapter adapter = buildAdapter(Map.of(MEMBER_ID, CONTACT_EMAIL));
        GenericRisFundingRestClient client = clientThrowing(RisFundingType.ONGOING_CALL, pe);

        assertThrows(ProcessingException.class, () -> adapter.fetchOnGoingCalls(client, MEMBER_ID));

        assertThat(captureNotificationTexts())
            .anyMatch(t -> t.contains("GRANT"));
    }

    private RisSynergyNetworkAdapter buildAdapter(Map<String, String> contactEmails) {
        given(config.getRegisteredRestClients()).willReturn(Collections.emptyMap());
        given(config.getContactEmails()).willReturn(contactEmails);
        return new RisSynergyNetworkAdapter(config, ticketingService, notificationService);
    }

    private GenericRisFundingRestClient clientThrowing(RisFundingType type, RuntimeException exception) {
        GenericRisFundingRestClient client = mock(GenericRisFundingRestClient.class);
        given(client.getMemberId()).willReturn(MEMBER_ID);
        given(client.getFundings(type)).willThrow(exception);
        return client;
    }

    private static ProcessingException plainProcessingException() {
        return new ProcessingException("invalid format");
    }

    /**
     * Deserializes the given JSON as a RisFunding list using a real ObjectMapper, capturing the
     * resulting Jackson exception as a ProcessingException (mirroring what the REST client does).
     * Fails fast if the JSON unexpectedly parses without error.
     */
    private static ProcessingException parseFailure(String json) {
        try {
            OBJECT_MAPPER.readValue(json, FUNDING_LIST_TYPE);
            throw new IllegalStateException("Expected JSON deserialization to fail for: " + json);
        } catch (JsonProcessingException e) {
            return new ProcessingException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> captureNotificationTexts() {
        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(notificationService).sendSyncErrorNotification(eq(MEMBER_ID), eq(CONTACT_EMAIL), captor.capture());
        return captor.getValue();
    }
}
