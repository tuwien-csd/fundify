package at.ac.tuwien.fundify.bootstrap.e2e;


import static at.ac.tuwien.fundify.bootstrap.utils.TestConstants.TU_WIEN_UNIVERSITY_AFFILIATION;
import static io.restassured.RestAssured.given;

import at.ac.tuwien.fundify.adapters.in.rest.dto.VocabularyWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.resources.VocabularyResource;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating.UniversityMongoEntity;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating.VocabularyMongoEntity;
import at.ac.tuwien.fundify.bootstrap.utils.users.WithTUWUser;
import at.ac.tuwien.fundify.bootstrap.utils.users.WithUniVieApiUser;
import at.ac.tuwien.fundify.bootstrap.utils.users.WithUniVieUser;
import at.ac.tuwien.fundify.domain.common.EVocabularyType;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import java.util.Collections;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestHTTPEndpoint(VocabularyResource.class)
@TestSecurity(authorizationEnabled = false)
class VocabularyTest {

  private static String universityId;
  private static String keywordVocabularyId;

  @BeforeEach
  void setup() {
    UniversityMongoEntity.deleteAll();
    // persist a university and a vocabulary
    UniversityMongoEntity university = new UniversityMongoEntity();
    university.acronym = TU_WIEN_UNIVERSITY_AFFILIATION;
    university.persist();
    universityId = university.id.toHexString();

    VocabularyMongoEntity.deleteAll();
    VocabularyMongoEntity keywordVocabulary = new VocabularyMongoEntity();
    keywordVocabulary.universityId = new ObjectId(universityId);
    keywordVocabulary.entries = Collections.emptySet();
    keywordVocabulary.type = EVocabularyType.KEYWORD;
    keywordVocabulary.persist();
    keywordVocabularyId = keywordVocabulary.id.toHexString();

    VocabularyMongoEntity targetGroupVocabulary = new VocabularyMongoEntity();
    targetGroupVocabulary.universityId = new ObjectId(universityId);
    targetGroupVocabulary.entries = Collections.emptySet();
    targetGroupVocabulary.type = EVocabularyType.TARGETGROUP;
    targetGroupVocabulary.persist();
  }

  @Test
  @WithTUWUser
  void givenValidRequest_whenAddEntry_thenReturnsVocabularyIdAndStatus204() {
    String entryValue = "entry";

    given()
        .pathParam("id", keywordVocabularyId)
        .pathParam("value", entryValue)
        .contentType(ContentType.JSON)
        .put("/{id}/entries/{value}")
        .then()
        .statusCode(204);

  }

  @Test
  @WithTUWUser
  void givenValidId_whenAddEntryAndRetrieve_thenEntryIsAdded() {
    String entryValue = "entry";

    // Add new entry to vocabulary
    given()
        .pathParam("id", keywordVocabularyId)
        .pathParam("value", entryValue)
        .contentType(ContentType.JSON)
        .put("/{id}/entries/{value}")
        .then()
        .statusCode(204);

    // Retrieve the vocabulary and check if the new entry is present
    VocabularyWebModel responseVocabulary = given()
        .pathParam("id", keywordVocabularyId)
        .when()
        .get("/{id}")
        .then()
        .statusCode(200)
        .extract()
        .as(VocabularyWebModel.class);

    Assertions.assertTrue(responseVocabulary.entries().contains(entryValue));
  }

  @Test
  @WithTUWUser
  void givenInvalidId_whenAddEntry_thenReturnsNotFound() {
    String entryValue = "entry";

    given()
        .pathParam("id", new ObjectId().toHexString())
        .pathParam("value", entryValue)
        .contentType(ContentType.JSON)
        .put("/{id}/entries/{value}")
        .then()
        .statusCode(404);
  }

  @Test
  @WithTUWUser
  void givenValidRequest_whenDeleteEntry_thenReturnsStatus204() {
    String entryValue = "entryToDelete";

    given()
        .pathParam("id", keywordVocabularyId)
        .pathParam("value", entryValue)
        .contentType(ContentType.JSON)
        .delete("/{id}/entries/{value}")
        .then()
        .statusCode(204);
  }

  @Test
  @WithTUWUser
  void givenValidIdAndExistingEntry_whenDeleteEntryAndRetrieve_thenEntryIsRemoved() {
    String entryValue = "entryToDelete";

    // Initially add an entry to ensure it exists for deletion
    given()
        .pathParam("id", keywordVocabularyId)
        .pathParam("value", entryValue)
        .contentType(ContentType.JSON)
        .delete("/{id}/entries/{value}")
        .then()
        .statusCode(204);

    // Delete the entry from vocabulary
    given()
        .pathParam("id", keywordVocabularyId)
        .pathParam("value", entryValue)
        .contentType(ContentType.JSON)
        .delete("/{id}/entries/{value}")
        .then()
        .statusCode(204);

    // Retrieve the vocabulary to verify the entry has been removed
    VocabularyWebModel responseVocabulary = given()
        .pathParam("id", keywordVocabularyId)
        .when()
        .get("/{id}")
        .then()
        .statusCode(200)
        .extract()
        .as(VocabularyWebModel.class);

    Assertions.assertFalse(responseVocabulary.entries().contains(entryValue));
  }


  @Test
  @WithTUWUser
  void givenInvalidId_whenDeleteEntry_thenReturnsNotFound() {
    String entryValue = "entryToDelete";

    given()
        .pathParam("id", new ObjectId().toHexString())
        .pathParam("value", entryValue)
        .contentType(ContentType.JSON)
        .delete("/{id}/entries/{value}")
        .then()
        .statusCode(404);
  }

  @Test
  @WithTUWUser
  void givenValidId_whenGetVocabularyById_thenReturnsVocabularyAndStatus200() {
    String expectedInResponseId = keywordVocabularyId;

    VocabularyWebModel response = given()
        .pathParam("id", keywordVocabularyId)
        .when()
        .get("/{id}")
        .then()
        .statusCode(200)
        .extract()
        .as(VocabularyWebModel.class);

    Assertions.assertEquals(expectedInResponseId, response.id());
  }

  @Test
  @WithTUWUser
  void givenInvalidId_whenGetVocabularyById_thenReturnsNotFound() {
    given()
        .pathParam("id", new ObjectId().toHexString())
        .when()
        .get("/{id}")
        .then()
        .statusCode(404);
  }

  @Test
  @WithTUWUser
  void givenValidUniversityId_whenGetVocabularyByUniversityId_thenReturnsVocabulariesAndStatus200() {
    List<VocabularyWebModel> response = given()
        .queryParam("universityId", universityId)
        .when()
        .get("/")
        .then()
        .statusCode(200)
        .extract()
        .jsonPath().getList(".", VocabularyWebModel.class);

    Assertions.assertEquals(2, response.size());
  }

  @Test
  @WithTUWUser
  void givenUninitializedVocublary_whenGetAll_thenInitializesAndReturnsVocabularies() {
    List<VocabularyWebModel> response = given()
        .when()
        .get("/")
        .then()
        .statusCode(200)
        .extract()
        .jsonPath().getList(".", VocabularyWebModel.class);

    Assertions.assertEquals(2, response.size());


  }

  @Nested
  @QuarkusTest
  @TestHTTPEndpoint(VocabularyResource.class)
  class PermissionTests {

    @Test
    @WithUniVieApiUser
    void when_Add_Entry_AsUnaffiliatedUser_then_ReturnForbidden() {

      given()
          .pathParam("id", keywordVocabularyId)
          .pathParam("value", "unauthorized-entry")
          .contentType(ContentType.JSON)
          .put("/{id}/entries/{value}")
          .then()
          .statusCode(403);
    }

    @Test
    @WithUniVieApiUser
    void when_Delete_Entry_AsUnaffiliatedUser_then_ReturnForbidden() {
      given()
          .pathParam("id", keywordVocabularyId)
          .pathParam("value", "some-entry")
          .contentType(ContentType.JSON)
          .delete("/{id}/entries/{value}")
          .then()
          .statusCode(403);
    }


    @Test
    @WithUniVieUser
    void when_GetVocabularyById_AsUnaffiliatedUser_thenReturns404() {
      given()
          .pathParam("id", keywordVocabularyId)
          .when()
          .get("/{id}")
          .then()
          .statusCode(404);
    }
  }
}