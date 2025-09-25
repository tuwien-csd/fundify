package at.ac.tuwien.refop.bootstrap.e2e;

import static at.ac.tuwien.refop.adapters.in.rest.constants.FundingEntityMethodPath.ADD_ENTITY;
import static at.ac.tuwien.refop.adapters.in.rest.constants.FundingEntityMethodPath.DELETE_ENTITY_BY_ID_REPLACE_PARAMTER;
import static at.ac.tuwien.refop.adapters.in.rest.constants.FundingEntityMethodPath.ENTITY_BY_ID_REPLACE_PARAMETER;
import static at.ac.tuwien.refop.adapters.in.rest.constants.FundingEntityMethodPath.ENTITY_LIST;
import static at.ac.tuwien.refop.adapters.in.rest.constants.FundingEntityMethodPath.QUERY_PARAM_STATUS;
import static at.ac.tuwien.refop.adapters.in.rest.constants.FundingEntityMethodPath.UPDATE_ENTITY;
import static at.ac.tuwien.refop.bootstrap.utils.TestConstants.FFG_FUNDER_AFFILIATION;
import static io.restassured.RestAssured.given;

import at.ac.tuwien.refop.adapters.in.rest.dto.FunderRefWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.ProgramWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.EEntryOriginWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.EPublicationStatusWebModel;
import at.ac.tuwien.refop.adapters.in.rest.resources.ProgramResource;
import at.ac.tuwien.refop.adapters.out.persistence.mongo.funding.FunderMongoEntity;
import at.ac.tuwien.refop.adapters.out.persistence.mongo.funding.ProgramMongoEntity;
import at.ac.tuwien.refop.adapters.out.persistence.mongo.funding.ProgramMongoRepository;
import at.ac.tuwien.refop.bootstrap.utils.users.WithAdminUser;
import at.ac.tuwien.refop.bootstrap.utils.users.WithFFGFunderUser;
import at.ac.tuwien.refop.bootstrap.utils.users.WithXYZFunderUser;
import at.ac.tuwien.refop.domain.common.ELanguage;
import at.ac.tuwien.refop.domain.common.EPublicationStatus;
import at.ac.tuwien.refop.domain.common.ETranslation;
import at.ac.tuwien.refop.domain.common.ProgramId;
import at.ac.tuwien.refop.domain.common.TranslatedText;
import at.ac.tuwien.refop.domain.funding.vo.enums.EEntryOrigin;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestHTTPEndpoint(ProgramResource.class)
@TestSecurity(authorizationEnabled = false)
class ProgramTest {

    @Inject
    ProgramMongoRepository programMongoRepository;

    private static String funderId;
    private static String programId;
    private static String programIdManagedBySync;

    @BeforeAll
    static void initAll() {

        FunderMongoEntity funder = new FunderMongoEntity();
        funder.persist();
        funderId = funder.id.toHexString();

        ProgramMongoEntity program = new ProgramMongoEntity();
        program.persist();
        programId = program.id.toHexString();

        ProgramMongoEntity programManagedBySync = new ProgramMongoEntity();
        programManagedBySync.persist();
        programIdManagedBySync = programManagedBySync.id.toHexString();
    }

    @BeforeEach
    void setUp() {
        FunderMongoEntity existingFunder = new FunderMongoEntity();
        existingFunder.id = new ObjectId(funderId);
        existingFunder.acronym = FFG_FUNDER_AFFILIATION;
        existingFunder.website = "https://www.ffg.org";
        existingFunder.name = List.of(
          new TranslatedText("FFG", ELanguage.GERMAN, ETranslation.ORIGINAL));
        existingFunder.persistOrUpdate();

        ProgramMongoEntity existingProgram = new ProgramMongoEntity();
        existingProgram.id = new ObjectId(programId);
        existingProgram.funderId = new ObjectId(funderId);
        existingProgram.status = EPublicationStatus.DRAFT;
        existingProgram.persistOrUpdate();

        ProgramMongoEntity existingProgramManagedBySync = new ProgramMongoEntity();
        existingProgramManagedBySync.id = new ObjectId(programIdManagedBySync);
        existingProgramManagedBySync.entryOrigin = EEntryOrigin.ENDPOINT;
        existingProgramManagedBySync.funderId = new ObjectId(funderId);
        existingProgramManagedBySync.status = EPublicationStatus.DRAFT;
        existingProgramManagedBySync.persistOrUpdate();
    }

    @Test
    @WithFFGFunderUser
    void givenProgramWithStatusDraft_whenAdd_thenReturnsSameProgramWithId() {
        final ProgramWebModel requestProgram = generateTestProgram(
                null,
                null,
                FFG_FUNDER_AFFILIATION,
                funderId,
                EPublicationStatusWebModel.DRAFT);

        ProgramWebModel responseProgram = given()
                .contentType(ContentType.JSON)
                .body(requestProgram)
                .when()
                .post(ADD_ENTITY)
                .then()
                .statusCode(200)
                .extract()
                .as(ProgramWebModel.class);

        Assertions.assertNotNull(responseProgram.id());
        Assertions.assertNull(responseProgram.risId());
        Assertions.assertEquals(requestProgram.acronym(), responseProgram.acronym());
    }

    @Test
    @WithFFGFunderUser
    void givenProgramWithStatusDraft_whenAdd_thenNoRegistrationDateAndLastSync() {
        final ProgramWebModel requestProgram = generateTestProgram(
                null,
                null,
                FFG_FUNDER_AFFILIATION,
                funderId,
                EPublicationStatusWebModel.DRAFT);

        ProgramWebModel responseProgram = given()
                .contentType(ContentType.JSON)
                .body(requestProgram)
                .when()
                .post(ADD_ENTITY)
                .then()
                .statusCode(200)
                .extract()
                .as(ProgramWebModel.class);

        ProgramMongoEntity result = ProgramMongoEntity.findById(new ObjectId(responseProgram.id()));

        Assertions.assertNull(result.registrationDate);
        Assertions.assertNull(result.lastSync);
    }

    @Test
    @WithFFGFunderUser
    void givenProgramWithStatusPublished_whenAdd_thenAssignRegistrationDateAndLastSync() {
        final ProgramWebModel requestProgram = generateTestProgram(
                null,
                null,
                FFG_FUNDER_AFFILIATION,
                funderId,
                EPublicationStatusWebModel.PUBLISHED);

        ProgramWebModel responseProgram = given()
                .contentType(ContentType.JSON)
                .body(requestProgram)
                .when()
                .post(ADD_ENTITY)
                .then()
                .statusCode(200)
                .extract()
                .as(ProgramWebModel.class);

        ProgramMongoEntity result = ProgramMongoEntity.findById(new ObjectId(responseProgram.id()));

        Assertions.assertNotNull(result.registrationDate);
        Assertions.assertNotNull(result.lastSync);
        Assertions.assertEquals(result.registrationDate, result.lastSync);
    }

    @Test
    @WithFFGFunderUser
    void givenProgramWithStatusPublished_whenAdd_thenReturnSameProgramWithIdAndRisId() {
        final ProgramWebModel requestProgram = generateTestProgram(
                null,
                null,
                FFG_FUNDER_AFFILIATION,
                funderId,
                EPublicationStatusWebModel.PUBLISHED);

        ProgramWebModel responseProgram = given()
                .contentType(ContentType.JSON)
                .body(requestProgram)
                .when()
                .post(ADD_ENTITY)
                .then()
                .statusCode(200)
                .extract()
                .as(ProgramWebModel.class);

        Assertions.assertNotNull(responseProgram.id());
        Assertions.assertNotNull(responseProgram.risId());
        Assertions.assertEquals(requestProgram.acronym(), responseProgram.acronym());
    }

    @Test
    @WithFFGFunderUser
    void givenPublishedProgramWithRisId_whenAdd_thenReturnBadRequest() {

        final ProgramWebModel requestProgram = generateTestProgram(
                null,
                "ris:TEST:funding:program1",
                FFG_FUNDER_AFFILIATION,
                funderId,
                EPublicationStatusWebModel.PUBLISHED);

        given()
                .contentType(ContentType.JSON)
                .body(requestProgram)
                .when()
                .post(ADD_ENTITY)
                .then()
                .statusCode(400);
    }

    @Test
    @WithFFGFunderUser
    void givenDraftProgram_whenUpdate_thenReturnUpdatedProgram() {

        ProgramWebModel requestProgram = generateTestProgram(
                programId,
                null,
                FFG_FUNDER_AFFILIATION,
                funderId,
                EPublicationStatusWebModel.DRAFT);

        ProgramWebModel updatedProgram = given()
                .contentType(ContentType.JSON)
                .body(requestProgram)
                .when()
                .put(UPDATE_ENTITY)
                .then()
                .statusCode(200)
                .extract()
                .as(ProgramWebModel.class);

        Assertions.assertEquals(requestProgram.id(), updatedProgram.id());
        Assertions.assertEquals(requestProgram.acronym(), updatedProgram.acronym());
    }

    @Test
    @WithFFGFunderUser
    void givenPublishedProgram_whenUpdate_thenUpdateLastSync() {
        ProgramMongoEntity existingProgram = new ProgramMongoEntity();
        existingProgram.id = new ObjectId(programId);
        existingProgram.funderId = new ObjectId(funderId);
        existingProgram.status = EPublicationStatus.PUBLISHED;
        existingProgram.update();

        LocalDateTime controlDate = LocalDateTime.now();

        ProgramWebModel requestProgram = generateTestProgram(
                existingProgram.id.toHexString(),
                null,
                FFG_FUNDER_AFFILIATION,
                funderId,
                EPublicationStatusWebModel.PUBLISHED);

        ProgramWebModel responseProgram = given()
                .contentType(ContentType.JSON)
                .body(requestProgram)
                .when()
                .put(UPDATE_ENTITY)
                .then()
                .statusCode(200)
                .extract()
                .as(ProgramWebModel.class);

        Assertions.assertTrue(controlDate.isBefore(responseProgram.lastSync()));
    }

    @Test
    @WithFFGFunderUser
    void givenUnknownProgram_whenUpdate_thenReturnNotFound() {

        final String validId = "5f3e3e3e3e3e3e3e3e3e3e3e";
        final ProgramWebModel requestProgram = generateTestProgram(
                validId,
                null,
                FFG_FUNDER_AFFILIATION,
                funderId,
                EPublicationStatusWebModel.DRAFT);

        given()
                .contentType(ContentType.JSON)
                .body(requestProgram)
                .when()
                .put(UPDATE_ENTITY)
                .then()
                .statusCode(404);
    }

    @Test
    @WithFFGFunderUser
    void givenProgramId_whenDelete_thenProgramIsDeleted() {

        given()
                .when()
                .delete(DELETE_ENTITY_BY_ID_REPLACE_PARAMTER, programId)
                .then()
                .statusCode(204);

        Assertions.assertTrue(programMongoRepository.findById(new ProgramId(programId)).isEmpty());
    }

    @Test
    @WithFFGFunderUser
    void givenStatus_whenListAll_thenReturnProgramsWithStatus() {
        ProgramMongoEntity.deleteAll();
        FunderMongoEntity funder = new FunderMongoEntity();
        funder.persist();
        ProgramMongoEntity program1 = new ProgramMongoEntity();
        program1.status = EPublicationStatus.PUBLISHED;
        program1.funderId = funder.id;
        program1.persist();
        ProgramMongoEntity program2 = new ProgramMongoEntity();
        program2.status = EPublicationStatus.DRAFT;
        program2.funderId = funder.id;
        program2.persist();

        ProgramWebModel[] programs = given()
                .when()
                .queryParam(QUERY_PARAM_STATUS, EPublicationStatusWebModel.PUBLISHED)
                .get(ENTITY_LIST)
                .then()
                .statusCode(200)
                .extract()
                .as(ProgramWebModel[].class);

        Assertions.assertEquals(1, programs.length);
        Assertions.assertEquals(EPublicationStatusWebModel.PUBLISHED, programs[0].status());
    }

    @Test
    @WithFFGFunderUser
    void givenId_whenGetById_thenReturnProgramWithId() {
        ProgramMongoEntity program = new ProgramMongoEntity();
        program.persist();

        ProgramWebModel responseProgram = given()
                .when()
                .get(ENTITY_BY_ID_REPLACE_PARAMETER, program.id.toHexString())
                .then()
                .statusCode(200)
                .extract()
                .as(ProgramWebModel.class);

        Assertions.assertEquals(program.id.toHexString(), responseProgram.id());
    }

    @Test
    @WithFFGFunderUser
    void givenUnkownId_whenGetById_thenReturnNotFound() {
        String unknownId = "5f3e3e3e3e3e3e3e3e3e3e3e";

        given()
                .when()
                .get(ENTITY_BY_ID_REPLACE_PARAMETER, unknownId)
                .then()
                .statusCode(404);
    }

    @Test
    @WithFFGFunderUser
    void givenInvalidId_whenGetById_thenReturnBadRequest() {
        String invalidId = "invalidId";
        given()
                .when()
                .get(ENTITY_BY_ID_REPLACE_PARAMETER, invalidId)
                .then()
                .statusCode(404);
    }


  @Nested
  @QuarkusTest
  @TestHTTPEndpoint(ProgramResource.class)
  class PermissionTests {

    @Test
    @WithXYZFunderUser
    void when_Add_ProgramAsUnaffiliatedFunder_then_returnForbidden() {
      final var requestProgram = generateTestProgram(
          null,
          null,
          FFG_FUNDER_AFFILIATION,
          funderId,
          EPublicationStatusWebModel.DRAFT);

      given()
          .contentType(ContentType.JSON)
          .body(requestProgram)
          .when()
          .post(ADD_ENTITY)
          .then()
          .statusCode(403);
    }

    @Test
    @WithAdminUser
    void when_Add_ProgramAsAdmin_thenReturnOk() {
      final var requestProgram = generateTestProgram(
          null,
          null,
          FFG_FUNDER_AFFILIATION,
          funderId,
          EPublicationStatusWebModel.DRAFT);

      given()
          .contentType(ContentType.JSON)
          .body(requestProgram)
          .when()
          .post(ADD_ENTITY)
          .then()
          .statusCode(200);
    }


    @Test
    @WithXYZFunderUser
    void when_Update_DraftProgramAsUnaffiliatedUser_then_ReturnForbidden() {

      final var requestProgram = generateTestProgram(
          programId,
          null,
          FFG_FUNDER_AFFILIATION,
          funderId,
          EPublicationStatusWebModel.DRAFT);

      given()
          .contentType(ContentType.JSON)
          .body(requestProgram)
          .when()
          .put(UPDATE_ENTITY)
          .then()
          .statusCode(403);
    }
    @Test
    @WithAdminUser
    void when_Update_ProgramManagedThroughSync_then_ReturnForbidden() {

      final var requestProgram = generateTestProgram(
          programIdManagedBySync,
          null,
          FFG_FUNDER_AFFILIATION,
          programIdManagedBySync,
          EPublicationStatusWebModel.DRAFT);

      given()
          .contentType(ContentType.JSON)
          .body(requestProgram)
          .when()
          .put(UPDATE_ENTITY)
          .then()
          .statusCode(403);
    }


    @Test
    @WithXYZFunderUser
    void when_Delete_ProgramAsUnaffiliatedUser_then_ReturnForbidden() {
      given()
          .when()
          .delete(DELETE_ENTITY_BY_ID_REPLACE_PARAMTER, programId)
          .then()
          .statusCode(403);
    }

    @Test
    @WithAdminUser
    void when_Delete_ProgramManagedThroughSync_then_ReturnForbidden() {
      given()
          .when()
          .delete(DELETE_ENTITY_BY_ID_REPLACE_PARAMTER, programIdManagedBySync)
          .then()
          .statusCode(403);
    }
  }

    private ProgramWebModel generateTestProgram(
            String id,
            String risId,
            String acronym,
            String funderId,
            EPublicationStatusWebModel status
    ) {
        return new ProgramWebModel(
                id,
                status,
                EEntryOriginWebModel.REFOP,
                null,
                null,
                risId,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new FunderRefWebModel(funderId, null, null, null, null, acronym, null),
                acronym,
                null,
                null,
                null,
                null
        );
    }
}