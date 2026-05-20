package at.ac.tuwien.fundify.bootstrap.e2e;

import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.BY_ID;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.REFERENCE_BY_ID;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.REFERENCE_LIST;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.REFERENCE_SEARCH;
import static io.restassured.RestAssured.given;

import at.ac.tuwien.fundify.adapters.in.rest.dto.FunderRefWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.FunderWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.PostAddressWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.TranslatedTextWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.ELanguageWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.ETranslationWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.resources.FunderResource;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding.FunderMongoEntity;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding.FunderMongoRepository;
import at.ac.tuwien.fundify.bootstrap.utils.users.WithAdminUser;
import at.ac.tuwien.fundify.domain.common.ELanguage;
import at.ac.tuwien.fundify.domain.common.ETranslation;
import at.ac.tuwien.fundify.domain.common.FunderId;
import at.ac.tuwien.fundify.domain.common.RisId;
import at.ac.tuwien.fundify.domain.common.TranslatedText;
import at.ac.tuwien.fundify.domain.funding.Funder;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestHTTPEndpoint(FunderResource.class)
@TestSecurity(authorizationEnabled = false)
class FunderTest {

  @Inject
  FunderMongoRepository funderMongoRepository;

  @BeforeEach
  void setUp() {
    funderMongoRepository.deleteAll();
  }

  @Test
  @WithAdminUser
  void givenFunder_whenAdd_thenReturnsSameFunderWithId() {
    final FunderWebModel requestFunder = generateTestFunderWebModel(null, "testAcronym");

    FunderWebModel responseFunder = given()
        .contentType(ContentType.JSON)
        .body(requestFunder)
        .when()
        .post()
        .then()
        .statusCode(200)
        .extract()
        .as(FunderWebModel.class);

    Assertions.assertNotNull(responseFunder.id());
    Assertions.assertEquals(requestFunder.acronym(), responseFunder.acronym());
  }

  @Test
  @WithAdminUser
  void givenFunder_whenUpdate_thenReturnsUpdatedFunder() {
    final FunderMongoEntity funder = this.generateTestFunderMongoEntity();
    funder.persist();
    final FunderWebModel requestFunder = generateTestFunderWebModel(funder.id.toHexString(),
        "testAcronym");

    FunderWebModel updatedFunder = given()
        .contentType(ContentType.JSON)
        .body(requestFunder)
        .when()
        .put(funder.id.toHexString())
        .then()
        .statusCode(200)
        .extract()
        .as(FunderWebModel.class);

    Assertions.assertNotNull(updatedFunder.id());
    Assertions.assertEquals(requestFunder.acronym(), updatedFunder.acronym());
  }

  @Test
  @WithAdminUser
  void givenNonExistentFunder_whenUpdate_thenReturnsNotFound() {
    final String validId = "5f3e3e3e3e3e3e3e3e3e3e3e";
    final FunderWebModel requestFunder = generateTestFunderWebModel(validId, "testAcronym");

    given()
        .contentType(ContentType.JSON)
        .body(requestFunder)
        .when()
        .put(validId)
        .then()
        .statusCode(404);
  }

  @Test
  @WithAdminUser
  void givenFunderId_whenDelete_thenFunderIsDeleted() {
    final FunderMongoEntity funder = new FunderMongoEntity();
    funder.persist();

    String funderId = funder.id.toHexString();

    given()
        .when()
        .delete(BY_ID, funderId)
        .then()
        .statusCode(204);

    Optional<Funder> deletedFunder = funderMongoRepository.findById(new FunderId(funderId));
    Assertions.assertTrue(deletedFunder.isEmpty());
  }

  @Test
  @WithAdminUser
  void givenDeletedFunderId_whenDelete_thenReturnsNotFound() {
    final FunderMongoEntity funder = new FunderMongoEntity();
    funder.persist();

    String funderId = funder.id.toHexString();

    // Delete the funder
    given()
        .pathParam("id", funderId)
        .when()
        .delete(BY_ID)
        .then()
        .statusCode(204);

    // Attempt to delete the same funder again to test idempotency
    given()
        .pathParam("id", funderId)
        .when()
        .delete(BY_ID)
        .then()
        .statusCode(404);
  }

  @Test
  @WithAdminUser
  void givenFunderList_whenList_thenReturnList() {
    final FunderMongoEntity funder1 = this.generateTestFunderMongoEntity();
    funder1.persist();
    final FunderMongoEntity funder2 = this.generateTestFunderMongoEntity();
    funder2.persist();

    List<FunderWebModel> funders = given()
        .when()
        .get()
        .then()
        .statusCode(200)
        .extract()
        .body()
        .jsonPath()
        .getList(".", FunderWebModel.class);

    Assertions.assertFalse(funders.isEmpty());
    Assertions.assertTrue(funders.stream().anyMatch(f -> f.id().equals(funder1.id.toHexString())));
    Assertions.assertTrue(funders.stream().anyMatch(f -> f.id().equals(funder2.id.toHexString())));
  }

  @Test
  @WithAdminUser
  void givenFunderList_whenListReferences_thenReturnReferenceList() {
    final FunderMongoEntity funder1 = this.generateTestFunderMongoEntity();
    funder1.persist();
    final FunderMongoEntity funder2 = this.generateTestFunderMongoEntity();
    funder2.persist();

    List<FunderRefWebModel> funderRefs = given()
        .when()
        .get(REFERENCE_LIST)
        .then()
        .statusCode(200)
        .extract()
        .body()
        .jsonPath()
        .getList(".", FunderRefWebModel.class);

    Assertions.assertFalse(funderRefs.isEmpty());
    Assertions.assertTrue(
        funderRefs.stream().anyMatch(f -> f.id().equals(funder1.id.toHexString())));
    Assertions.assertTrue(
        funderRefs.stream().anyMatch(f -> f.id().equals(funder2.id.toHexString())));
  }

  @Test
  @WithAdminUser
  void givenFunderId_whenGetById_thenReturnFunder() {
    final FunderMongoEntity funder = this.generateTestFunderMongoEntity();
    funder.persist();

    FunderWebModel result = given()
        .when()
        .get(BY_ID, funder.id.toHexString())
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(FunderWebModel.class);

    Assertions.assertEquals(funder.id.toHexString(), result.id());
  }

  @Test
  @WithAdminUser
  void givenFunderId_whenGetReferenceById_thenReturnFunderReference() {
    final FunderMongoEntity funder = this.generateTestFunderMongoEntity();
    funder.persist();

    FunderRefWebModel result = given()
        .when()
        .get(REFERENCE_BY_ID, funder.id.toHexString())
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(FunderRefWebModel.class);

    Assertions.assertEquals(funder.id.toHexString(), result.id());
  }

  @Test
  @WithAdminUser
  void givenQueryAndMatchingFunder_whenSearch_thenReturnMatchingFunder() {
    final FunderMongoEntity funder1 = new FunderMongoEntity();
    funder1.acronym = "searchTerm";

    funder1.persist();
    final FunderMongoEntity funder2 = new FunderMongoEntity();
    funder2.persist();

    List<FunderRefWebModel> results = given()
        .queryParam("term", "searchTerm")
        .when()
        .get(REFERENCE_SEARCH)
        .then()
        .statusCode(200)
        .extract()
        .body()
        .jsonPath()
        .getList(".", FunderRefWebModel.class);

    Assertions.assertFalse(results.isEmpty());
    Assertions.assertTrue(results.stream().anyMatch(f -> f.id().equals(funder1.id.toHexString())));
  }

  private FunderWebModel generateTestFunderWebModel(String id, String acronym) {
    return new FunderWebModel(
        id,
        List.of(new TranslatedTextWebModel("Test Funder", ELanguageWebModel.ENGLISH,
            ETranslationWebModel.ORIGINAL)),
        id != null ? RisId.generateFundingRisId(id, acronym).toString() : null,
        "www.example.com",
        acronym,
        null,
        null,
        null,
        null,
            new PostAddressWebModel("Test Street", "Test City", "1234", "AT"),
        false
    );
  }

  private FunderMongoEntity generateTestFunderMongoEntity() {
    FunderMongoEntity funder = new FunderMongoEntity();
    funder.acronym = "TestAcronym";
    funder.name = List.of(
        new TranslatedText("Test Funder", ELanguage.ENGLISH, ETranslation.ORIGINAL));
    funder.website = "https://example.com";
    return funder;
  }
}