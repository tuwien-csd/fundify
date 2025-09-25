package at.ac.tuwien.refop.bootstrap.e2e;

import static at.ac.tuwien.refop.bootstrap.utils.TestConstants.TU_WIEN_UNIVERSITY_AFFILIATION;
import static at.ac.tuwien.refop.bootstrap.utils.TestConstants.UNI_VIE_UNIVERSITY_AFFILIATION;
import static at.ac.tuwien.refop.bootstrap.utils.TestConstants.TUW_USER_NAME;
import static at.ac.tuwien.refop.bootstrap.utils.TestConstants.TUW_USER_ID;
import static io.restassured.RestAssured.given;

import at.ac.tuwien.refop.adapters.in.rest.dto.AnnotateRequest;
import at.ac.tuwien.refop.adapters.in.rest.dto.AnnotatedCallWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.CallAnnotationWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.EPublicationStatusWebModel;
import at.ac.tuwien.refop.adapters.in.rest.resources.AnnotatedCallResource;
import at.ac.tuwien.refop.adapters.out.persistence.mongo.annotating.AnnotatedCallMongoEntity;
import at.ac.tuwien.refop.adapters.out.persistence.mongo.annotating.UniversityMongoEntity;
import at.ac.tuwien.refop.adapters.out.persistence.mongo.funding.CallMongoEntity;
import at.ac.tuwien.refop.bootstrap.utils.users.WithAdminUser;
import at.ac.tuwien.refop.bootstrap.utils.users.WithTUWUser;
import at.ac.tuwien.refop.bootstrap.utils.users.WithUniVieUser;
import at.ac.tuwien.refop.domain.annotating.AnnotatedCallId;
import at.ac.tuwien.refop.domain.annotating.CallAnnotation;
import at.ac.tuwien.refop.domain.common.EPublicationStatus;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


@QuarkusTest
@TestHTTPEndpoint(AnnotatedCallResource.class)
@TestSecurity(authorizationEnabled = false)
class AnnotatedCallTest {

    private static final String ANNOTATED_CALL_ID = new ObjectId().toHexString();
    private static final String TUW_UNIVERSITY_ID = new ObjectId().toHexString();
    private static final String UNIVIE_UNIVERSITY_ID = new ObjectId().toHexString();
    private static final String CALL_ID = new ObjectId().toHexString();


    @BeforeEach
    void setUp() {
        UniversityMongoEntity tuwUniversity = new UniversityMongoEntity();
        tuwUniversity.id = new ObjectId(TUW_UNIVERSITY_ID);
        tuwUniversity.acronym = TU_WIEN_UNIVERSITY_AFFILIATION;
        tuwUniversity.persistOrUpdate();

        UniversityMongoEntity uniVieUniversity = new UniversityMongoEntity();
        uniVieUniversity.id = new ObjectId(UNIVIE_UNIVERSITY_ID);
        uniVieUniversity.acronym = UNI_VIE_UNIVERSITY_AFFILIATION;
        uniVieUniversity.persistOrUpdate();

        CallMongoEntity existingCall = new CallMongoEntity();
        existingCall.id = new ObjectId(CALL_ID);
        existingCall.funderId = new ObjectId();
        existingCall.status = EPublicationStatus.DRAFT;
        existingCall.subscriptions = new ArrayList<>();
        existingCall.persistOrUpdate();

      createAnnotatedCallFor(new ObjectId(ANNOTATED_CALL_ID), new ObjectId(TUW_UNIVERSITY_ID));
    }

    @Test
    @WithTUWUser
    void givenValidRequest_whenAnnotate_thenReturnsAnnotatedCallIdAndStatus200() {
        AnnotateRequest request = new AnnotateRequest(CALL_ID, TUW_UNIVERSITY_ID, mockCallAnnotationWebModel());

        // remove existing annotated calls for the CALL_ID and UNIVERSITY_ID
        AnnotatedCallMongoEntity.deleteAll();

        AnnotatedCallId response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/")
                .then()
                .statusCode(200)
                .extract()
                .as(AnnotatedCallId.class);

        Assertions.assertNotNull(response);
    }

    @Test
    @WithTUWUser
    void givenExistingAnnotatedCall_whenAnnotate_thenReturnsBadRequest() {
        AnnotateRequest request = new AnnotateRequest(CALL_ID, TUW_UNIVERSITY_ID, mockCallAnnotationWebModel());

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/")
                .then()
                .statusCode(400);
    }

    @Test
    @WithTUWUser
    void givenValidRequest_whenUpdateAnnotation_thenReturnsAnnotatedCallIdAndStatus200() {
        CallAnnotationWebModel annotation = mockCallAnnotationWebModel();
        AnnotatedCallId expectedResponse = new AnnotatedCallId(ANNOTATED_CALL_ID);

        AnnotatedCallId response = given()
                .contentType(ContentType.JSON)
                .body(annotation)
                .when()
                .put("/" + ANNOTATED_CALL_ID)
                .then()
                .statusCode(200)
                .extract()
                .as(AnnotatedCallId.class);

        Assertions.assertEquals(expectedResponse, response);
    }

    @Test
    @WithTUWUser
    void givenInvalidId_whenUpdateAnnotation_thenReturnsNotFound() {
        CallAnnotationWebModel annotationToUpdate = new CallAnnotationWebModel(
                List.of(),
                List.of("keyword"),
                List.of("targetGroup"),
                List.of(),
                null);

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", new ObjectId().toHexString())
                .body(annotationToUpdate)
                .when()
                .put("/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    @WithTUWUser
    void givenValidIdAndUpdates_whenUpdateAndRetrieve_thenAnnotationsAreUpdated() {
        CallAnnotationWebModel annotationToUpdate = new CallAnnotationWebModel(
                List.of(),
                List.of("new keyword"),
                List.of("new targetGroup"),
                List.of(),
                null
        );

        // Update the annotation
        AnnotatedCallId updatedId = given()
                .contentType(ContentType.JSON)
                .pathParam("id", ANNOTATED_CALL_ID)
                .body(annotationToUpdate)
                .when()
                .put("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .as(AnnotatedCallId.class);

        Assertions.assertNotNull(updatedId);

        // Retrieve the updated annotated call
        AnnotatedCallWebModel retrievedCall = given()
                .contentType(ContentType.JSON)
                .pathParam("id", updatedId.value())
                .when()
                .get("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .as(AnnotatedCallWebModel.class);

        Assertions.assertTrue(retrievedCall.annotation().keywords().contains("new keyword"));
        Assertions.assertTrue(retrievedCall.annotation().targetGroups().contains("new targetGroup"));
    }


    @Test
    @WithTUWUser
    void givenValidId_whenDeleteAnnotation_thenReturnsTrueAndStatus200() {
        boolean response = given()
                .contentType(ContentType.JSON)
                .pathParam("id", ANNOTATED_CALL_ID)
                .when()
                .delete("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .as(Boolean.class);

        Assertions.assertTrue(response);
    }

    @Test
    @WithTUWUser
    void givenInvalidId_whenDeleteAnnotation_thenReturnsNotFound() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", new ObjectId().toHexString())
                .when()
                .delete("/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    @WithTUWUser
    void givenValidId_whenPublish_thenReturnsPublishedAnnotatedCallIdAndStatus200() {
        AnnotatedCallId expectedResponse = new AnnotatedCallId(ANNOTATED_CALL_ID);

        AnnotatedCallId response = given()
                .contentType(ContentType.JSON)
                .pathParam("id", ANNOTATED_CALL_ID)
                .when()
                .put("/{id}/publish")
                .then()
                .statusCode(200)
                .extract()
                .as(AnnotatedCallId.class);

        Assertions.assertEquals(expectedResponse, response);
    }

    @Test
    @WithTUWUser
    void givenInvalidId_whenPublish_thenReturnsNotFound() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", new ObjectId().toHexString())
                .when()
                .put("/{id}/publish")
                .then()
                .statusCode(404);
    }

    @Test
    @WithTUWUser
    void givenValidId_whenPublishAndRetrieve_thenStatusIsPublished() {
        // Publish the annotated call
        AnnotatedCallId publishedId = given()
                .contentType(ContentType.JSON)
                .pathParam("id", ANNOTATED_CALL_ID)
                .when()
                .put("/{id}/publish")
                .then()
                .statusCode(200)
                .extract()
                .as(AnnotatedCallId.class);

        Assertions.assertNotNull(publishedId);

        // Retrieve the published annotated call
        AnnotatedCallWebModel retrievedCall = given()
                .contentType(ContentType.JSON)
                .pathParam("id", publishedId.value())
                .when()
                .get("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .as(AnnotatedCallWebModel.class);

        Assertions.assertEquals(EPublicationStatusWebModel.PUBLISHED, retrievedCall.status());
    }

    @Test
    @WithTUWUser
    void givenValidId_whenGetAnnotatedCall_thenReturnsCorrectAnnotatedCallAndStatus200() {
        AnnotatedCallWebModel response = given()
                .contentType(ContentType.JSON)
                .pathParam("id", ANNOTATED_CALL_ID)
                .when()
                .get("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .as(AnnotatedCallWebModel.class);

        Assertions.assertEquals(ANNOTATED_CALL_ID, response.id());
        Assertions.assertEquals(TUW_UNIVERSITY_ID, response.university().id());
        Assertions.assertEquals(CALL_ID, response.callPreview().id());
    }

    @Test
    @WithTUWUser
    void when_GetAnnotatedCalls_AsAffiliatedUser_thenReturnsAnnotatedCallsAndStatus200() {
      int expected = 2; //One for TU is created in the setup, another one is created in this test.
      createAnnotatedCallFor(new ObjectId(), new ObjectId(TUW_UNIVERSITY_ID));

      createAnnotatedCallFor(new ObjectId(), new ObjectId(UNIVIE_UNIVERSITY_ID));

      AnnotatedCallWebModel[] response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/")
                .then()
                .statusCode(200)
                .extract()
                .as(AnnotatedCallWebModel[].class);

      Assertions.assertEquals(expected, response.length);
    }

    @Test
    @WithTUWUser
    void whenUpdateAnnotation_thenUpdateLastUpdatedAt() {
        CallAnnotationWebModel annotationToUpdate = new CallAnnotationWebModel(
                List.of(),
                List.of("new keyword"),
                List.of("new targetGroup"),
                List.of(),
                null
        );

        LocalDateTime dateBeforeUpdate = LocalDateTime.now();

        // Update the annotation
        AnnotatedCallId updatedId = given()
                .contentType(ContentType.JSON)
                .pathParam("id", ANNOTATED_CALL_ID)
                .body(annotationToUpdate)
                .when()
                .put("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .as(AnnotatedCallId.class);

        Assertions.assertNotNull(updatedId);
        LocalDateTime dateAfterUpdate = LocalDateTime.now();

        // Retrieve the updated annotated call
        AnnotatedCallWebModel retrievedCall = given()
                .contentType(ContentType.JSON)
                .pathParam("id", updatedId.value())
                .when()
                .get("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .as(AnnotatedCallWebModel.class);

        Assertions.assertTrue(dateBeforeUpdate.isBefore(retrievedCall.lastUpdatedAt()));
        Assertions.assertTrue(dateAfterUpdate.isAfter(retrievedCall.lastUpdatedAt()));
    }

    @Test
    @WithTUWUser
    void whenUpdateAnnotation_thenUpdateLastUpdatedBy() {
        CallAnnotationWebModel annotationToUpdate = new CallAnnotationWebModel(
                List.of(),
                List.of("new keyword"),
                List.of("new targetGroup"),
                List.of(),
                null
        );

        // Update the annotation
        AnnotatedCallId updatedId = given()
                .contentType(ContentType.JSON)
                .pathParam("id", ANNOTATED_CALL_ID)
                .body(annotationToUpdate)
                .when()
                .put("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .as(AnnotatedCallId.class);

        Assertions.assertNotNull(updatedId);

        // Retrieve the updated annotated call
        AnnotatedCallWebModel retrievedCall = given()
                .contentType(ContentType.JSON)
                .pathParam("id", updatedId.value())
                .when()
                .get("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .as(AnnotatedCallWebModel.class);

        Assertions.assertEquals(TUW_USER_ID, retrievedCall.lastUpdatedBy().id());
        Assertions.assertEquals(TUW_USER_NAME, retrievedCall.lastUpdatedBy().name());
        Assertions.assertEquals(TU_WIEN_UNIVERSITY_AFFILIATION, retrievedCall.lastUpdatedBy().affiliationId());
    }

  @Nested
  @QuarkusTest
  @TestHTTPEndpoint(AnnotatedCallResource.class)
  class PermissionTests {

    @Test
    @WithUniVieUser
    void when_Add_AnnotationAsUnaffiliatedUniversity_then_returnForbidden() {

      AnnotateRequest request = new AnnotateRequest(CALL_ID, TUW_UNIVERSITY_ID, mockCallAnnotationWebModel());
      // remove existing annotated calls for the CALL_ID and UNIVERSITY_ID
      AnnotatedCallMongoEntity.deleteAll();


      given()
          .contentType(ContentType.JSON)
          .body(request)
          .when()
          .post("/")
          .then()
          .statusCode(403);
    }
    @Test
    @WithAdminUser
    void when_Add_AnnotationAsAdminUser_then_returnForbidden() {

      AnnotateRequest request = new AnnotateRequest(CALL_ID, TUW_UNIVERSITY_ID, mockCallAnnotationWebModel());
      // remove existing annotated calls for the CALL_ID and UNIVERSITY_ID
      AnnotatedCallMongoEntity.deleteAll();


      given()
          .contentType(ContentType.JSON)
          .body(request)
          .when()
          .post("/")
          .then()
          .statusCode(403);
    }

    @Test
    @WithUniVieUser
    void when_Update_AnnotationAsUnaffiliatedUser_thenReturnsForbidden() {
      CallAnnotationWebModel annotation = mockCallAnnotationWebModel();

      given()
          .contentType(ContentType.JSON)
          .body(annotation)
          .when()
          .put("/" + ANNOTATED_CALL_ID)
          .then()
          .statusCode(403);
    }

    @Test
    @WithAdminUser
    void when_Update_AnnotationAsAdmin_thenReturnsForbidden() {
      CallAnnotationWebModel annotation = mockCallAnnotationWebModel();

      given()
          .contentType(ContentType.JSON)
          .body(annotation)
          .when()
          .put("/" + ANNOTATED_CALL_ID)
          .then()
          .statusCode(403);
    }



    @Test
    @WithUniVieUser
    void when_Delete_AnnotationAsUnaffiliatedUser_thenReturnsForbidden() {
          given()
          .contentType(ContentType.JSON)
          .pathParam("id", ANNOTATED_CALL_ID)
          .when()
          .delete("/{id}")
          .then()
          .statusCode(403);
    }

    @Test
    @WithUniVieUser
    void when_Publish_AnnotationAsUnaffiliatedUser_thenReturnsForbidden() {
          given()
          .contentType(ContentType.JSON)
          .pathParam("id", ANNOTATED_CALL_ID)
          .when()
          .put("/{id}/publish")
          .then()
          .statusCode(403);
    }

    @Test
    @WithUniVieUser
    void when_GetAnnotatedCalls_AsUnaffiliatedUser_thenReturnsEmptyList() {
      // Create an annotation for TUW - should not be returned by the call
      createAnnotatedCallFor(new ObjectId(), new ObjectId(TUW_UNIVERSITY_ID));

      AnnotatedCallWebModel[] response = given()
          .contentType(ContentType.JSON)
          .when()
          .get("/")
          .then()
          .statusCode(200)
          .extract()
          .as(AnnotatedCallWebModel[].class);

      Assertions.assertEquals(0, response.length);
    }
  }

  private static void createAnnotatedCallFor(ObjectId annotationId, ObjectId universityId) {
    AnnotatedCallMongoEntity annotatedCallBySameUniversity = new AnnotatedCallMongoEntity();
    annotatedCallBySameUniversity.id = annotationId;
    annotatedCallBySameUniversity.callId = new ObjectId(CALL_ID);
    annotatedCallBySameUniversity.universityId = universityId;
    annotatedCallBySameUniversity.annotation = new CallAnnotation(null, null, null, null, null);
    annotatedCallBySameUniversity.persistOrUpdate();
  }


  private CallAnnotationWebModel mockCallAnnotationWebModel() {
        return new CallAnnotationWebModel(
                List.of(),
                List.of("keyword"),
                List.of("targetGroup"),
                List.of(),
                null);
    }
}