package at.ac.tuwien.fundify.bootstrap.e2e;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import at.ac.tuwien.fundify.adapters.in.rest.dto.TicketCreateWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.resources.ContactResource;
import at.ac.tuwien.fundify.application.port.in.registration.RegistrationRequestUseCase;
import at.ac.tuwien.fundify.application.port.out.ticketing.TicketingService;
import at.ac.tuwien.fundify.domain.common.exceptions.FundifyException;
import at.ac.tuwien.fundify.domain.ticketing.TicketCreate;
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

    @InjectMock
    RegistrationRequestUseCase registrationRequestUseCase;

    @Test
    void givenValidRequest_whenCreate_thenReturnsNoContentAndDelegatesToService() throws FundifyException {
        // arrange
        TicketCreateWebModel request = new TicketCreateWebModel(
                "John",
                "Doe",
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
        assertEquals(request.firstName(), captured.firstName());
        assertEquals(request.lastName(), captured.lastName());
        assertEquals(request.subject(), captured.subject());
        assertEquals(request.category(), captured.category());
        assertEquals(request.email(), captured.email());
        assertEquals(request.message(), captured.message());
        assertEquals(request.kindOfInstitution(), captured.kindOfInstitution());
        verifyNoInteractions(registrationRequestUseCase);
    }

    @Test
    void givenRegistrationCategory_whenCreate_thenCapturesRequestAndSkipsTicketing() {
        // arrange
        TicketCreateWebModel request = new TicketCreateWebModel(
                "Jane",
                "Doe",
                "Account please",
                "registration",
                "jane@funder.org",
                "I would like an account.",
                "Funder"
        );

        // act & assert HTTP
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post()
                .then()
                .statusCode(204);

        // registration requests are captured in Fundify, not filed as tickets
        verify(registrationRequestUseCase)
                .submit("Jane", "Doe", "jane@funder.org", "Funder", "I would like an account.");
        verifyNoInteractions(ticketingService);
    }
}
