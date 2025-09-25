package at.ac.tuwien.refop.bootstrap.e2e;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import at.ac.tuwien.refop.adapters.in.rest.dto.TicketCreateWebModel;
import at.ac.tuwien.refop.adapters.in.rest.resources.ContactResource;
import at.ac.tuwien.refop.application.port.out.ticketing.TicketingService;
import at.ac.tuwien.refop.domain.common.exceptions.FundifyException;
import at.ac.tuwien.refop.domain.ticketing.TicketCreate;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@QuarkusTest
@TestHTTPEndpoint(ContactResource.class)
@TestSecurity(authorizationEnabled = false)
class ContactTest {

    @InjectMock
    TicketingService ticketingService;

    @Test
    void givenValidRequest_whenCreate_thenReturnsNoContentAndDelegatesToService() throws FundifyException {
        // arrange
        TicketCreateWebModel request = new TicketCreateWebModel(
                "John Doe",
                "Issue with registration",
                "SUPPORT",
                "john.doe@example.com",
                "I cannot register to the platform.",
                "UNIVERSITY"
        );

        // act & assert HTTP
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post()
                .then()
                .statusCode(204);

        // verify delegation and mapping
        ArgumentCaptor<TicketCreate> captor = ArgumentCaptor.forClass(TicketCreate.class);
        verify(ticketingService).createTicket(captor.capture());
        TicketCreate captured = captor.getValue();
        assertEquals(request.name(), captured.name());
        assertEquals(request.subject(), captured.subject());
        assertEquals(request.category(), captured.category());
        assertEquals(request.email(), captured.email());
        assertEquals(request.message(), captured.message());
        assertEquals(request.kindOfInstitution(), captured.kindOfInstitution());
    }
}
