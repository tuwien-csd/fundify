package at.ac.tuwien.fundify.bootstrap.e2e;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import at.ac.tuwien.fundify.adapters.in.rest.resources.RegistrationRequestResource;
import at.ac.tuwien.fundify.application.port.in.registration.RegistrationRequestUseCase;
import at.ac.tuwien.fundify.domain.registration.RegistrationRequest;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestHTTPEndpoint(RegistrationRequestResource.class)
@TestSecurity(authorizationEnabled = false)
class RegistrationRequestTest {

  @InjectMock
  RegistrationRequestUseCase registrationRequestUseCase;

  @Test
  void getAll_returnsPendingRequests() {
    // arrange
    when(registrationRequestUseCase.getAll()).thenReturn(List.of(
        new RegistrationRequest("id-1", "Jane", "Doe", "jane@funder.org", "Funder", "Add me",
            Instant.parse("2026-06-24T08:00:00Z"))));

    // act & assert
    given()
        .when()
        .get()
        .then()
        .statusCode(200)
        .body("[0].id", is("id-1"))
        .body("[0].firstName", is("Jane"))
        .body("[0].lastName", is("Doe"))
        .body("[0].email", is("jane@funder.org"))
        .body("[0].kindOfInstitution", is("Funder"));
  }

  @Test
  void delete_removesRequestAndReturnsNoContent() {
    // act & assert HTTP
    given()
        .when()
        .delete("/id-1")
        .then()
        .statusCode(204);

    // verify delegation
    verify(registrationRequestUseCase).delete("id-1");
  }
}
