package at.ac.tuwien.fundify.adapters.out.ris.network;

import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFundingType;
import at.ac.tuwien.fundify.adapters.out.ris.network.client.GenericRisFundingRestClient;
import at.ac.tuwien.fundify.adapters.out.ris.network.config.RisClientConfiguration;
import at.ac.tuwien.fundify.application.port.out.notification.SyncErrorNotificationService;
import at.ac.tuwien.fundify.application.port.out.ticketing.TicketingService;
import jakarta.ws.rs.ProcessingException;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RisSynergyNetworkAdapterTest {

    private static final String MEMBER_ID = "fwf";
    private static final String CONTACT_EMAIL = "contact@fwf.ac.at";

    @Mock
    private TicketingService ticketingService;
    @Mock
    private RisClientConfiguration config;
    @Mock
    private SyncErrorNotificationService notificationService;

    @Test
    void fetchPrograms_sendsNotificationWhenContactEmailIsConfigured() {
        RisSynergyNetworkAdapter adapter = buildAdapter(Map.of(MEMBER_ID, CONTACT_EMAIL), notificationService);
        GenericRisFundingRestClient client = clientThrowingProcessingException();

        assertThrows(ProcessingException.class, () -> adapter.fetchPrograms(client, MEMBER_ID));

        verify(notificationService).sendSyncErrorNotification(eq(MEMBER_ID), eq(CONTACT_EMAIL), anyList());
    }

    @Test
    void fetchPrograms_skipsNotificationWhenNoContactEmailConfigured() {
        RisSynergyNetworkAdapter adapter = buildAdapter(Collections.emptyMap(), notificationService);
        GenericRisFundingRestClient client = clientThrowingProcessingException();

        assertThrows(ProcessingException.class, () -> adapter.fetchPrograms(client, MEMBER_ID));

        verifyNoInteractions(notificationService);
    }

    @Test
    void fetchPrograms_alwaysCreatesTicketOnProcessingException() {
        RisSynergyNetworkAdapter adapter = buildAdapter(Map.of(MEMBER_ID, CONTACT_EMAIL), notificationService);
        GenericRisFundingRestClient client = clientThrowingProcessingException();

        assertThrows(ProcessingException.class, () -> adapter.fetchPrograms(client, MEMBER_ID));

        verify(ticketingService).createOrAppend(
            argThat(ticket -> ticket.ticketKey().contains(MEMBER_ID.toUpperCase()))
        );
    }

    private RisSynergyNetworkAdapter buildAdapter(Map<String, String> contactEmails,
                                                  SyncErrorNotificationService notificationService) {
        given(config.getRegisteredRestClients()).willReturn(Collections.emptyMap());
        given(config.getContactEmails()).willReturn(contactEmails);
        return new RisSynergyNetworkAdapter(config, ticketingService, notificationService);
    }

    private GenericRisFundingRestClient clientThrowingProcessingException() {
        GenericRisFundingRestClient client = mock(GenericRisFundingRestClient.class);
        given(client.getMemberId()).willReturn(MEMBER_ID);
        given(client.getFundings(RisFundingType.PROGRAMME)).willThrow(new ProcessingException("invalid format"));
        return client;
    }
}
