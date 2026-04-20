package at.ac.tuwien.fundify.bootstrap.e2e;

import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.ADD_ENTITY;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.DELETE_ENTITY_BY_ID_REPLACE_PARAMTER;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.ENTITY_BY_ID_REPLACE_PARAMETER;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.ENTITY_LIST;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.ENTITY_UPDATE_SUBSCRIPTIONS;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.QUERY_PARAM_STATUS;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.UPDATE_ENTITY;
import static at.ac.tuwien.fundify.bootstrap.utils.TestConstants.*;
import static io.restassured.RestAssured.given;

import at.ac.tuwien.fundify.adapters.in.rest.dto.*;
import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.*;
import at.ac.tuwien.fundify.adapters.in.rest.resources.CallResource;
import at.ac.tuwien.fundify.adapters.out.fundify.EmailService;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating.UniversityMongoEntity;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding.CallMongoEntity;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding.FunderMongoEntity;
import at.ac.tuwien.fundify.bootstrap.utils.users.WithAdminUser;
import at.ac.tuwien.fundify.bootstrap.utils.users.WithFFGFunderUser;
import at.ac.tuwien.fundify.bootstrap.utils.users.WithTUWUser;
import at.ac.tuwien.fundify.bootstrap.utils.users.WithXYZFunderUser;
import at.ac.tuwien.fundify.domain.common.CallOwner;
import at.ac.tuwien.fundify.domain.common.CallId;
import at.ac.tuwien.fundify.domain.common.ELanguage;
import at.ac.tuwien.fundify.domain.common.EPublicationStatus;
import at.ac.tuwien.fundify.domain.common.ETranslation;
import at.ac.tuwien.fundify.domain.common.FundifyUser;
import at.ac.tuwien.fundify.domain.common.TranslatedText;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EEntryOrigin;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestHTTPEndpoint(CallResource.class)
@TestSecurity(authorizationEnabled = false)
class CallTest {

  private static String funderId;
  private static String externalFunderId;
  private static String callId;
  private static String callIdManagedBySync;
  private static String callIdExternal;
  private static String universityId;
  static final String SUBSCRIBED = "\"SUBSCRIBED\"";
  static final int BATCH_SIZE = 5;

  @Inject
  MockMailbox mailbox;

  @Inject
  EmailService emailService;

  @BeforeAll
  static void initAll() {
    FunderMongoEntity funder = new FunderMongoEntity();
    funder.persist();
    funderId = funder.id.toHexString();

    FunderMongoEntity externalFunder = new FunderMongoEntity();
    externalFunder.persist();
    externalFunderId = externalFunder.id.toHexString();
    externalFunder.externallyAdministered = true;

    CallMongoEntity call = new CallMongoEntity();
    call.persist();
    callId = call.id.toHexString();

    CallMongoEntity callManagedBySync = new CallMongoEntity();
    callManagedBySync.persist();
    callIdManagedBySync = callManagedBySync.id.toHexString();

    CallMongoEntity externalCall = new CallMongoEntity();
    externalCall.persist();
    callIdExternal = externalCall.id.toHexString();

    UniversityMongoEntity.deleteAll();
    UniversityMongoEntity universityEntityTUW = new UniversityMongoEntity();
    universityEntityTUW.acronym = TU_WIEN_UNIVERSITY_AFFILIATION;
    universityEntityTUW.persist();
    universityId = universityEntityTUW.id.toHexString();
  }

  @BeforeEach
  void setup() {
    FunderMongoEntity existingFunder = new FunderMongoEntity();
    existingFunder.id = new ObjectId(funderId);
    existingFunder.acronym = FFG_FUNDER_AFFILIATION;
    existingFunder.name = List.of(
        new TranslatedText("FFG", ELanguage.GERMAN, ETranslation.ORIGINAL));
    existingFunder.website = "https://www.ffg.org/";
    existingFunder.persistOrUpdate();

    FunderMongoEntity existingExternalFunder = new FunderMongoEntity();
    existingExternalFunder.id = new ObjectId(externalFunderId);
    existingExternalFunder.acronym = XYZ_FUNDER_AFFILIATION;
    existingExternalFunder.name = List.of(
          new TranslatedText("TEST_XYZ", ELanguage.GERMAN, ETranslation.ORIGINAL));
    existingExternalFunder.externallyAdministered = true;
    existingExternalFunder.website = "https://www.ffg.org/";
    existingExternalFunder.persistOrUpdate();

    CallMongoEntity existingCall = new CallMongoEntity();
    existingCall.id = new ObjectId(callId);
    existingCall.funderId = new ObjectId(funderId);
    existingCall.status = EPublicationStatus.DRAFT;
    existingCall.ownerKind = CallOwner.CallOwnerKind.FUNDER.toString();
    existingCall.ownerId = funderId;
    existingCall.subscriptions = new ArrayList<>();
    existingCall.subscriptions = List.of(
        new FundifyUser("1f3e3e3e3e3e3e3e3e3e3e3e", "Chris", null, "foo1@quarkus.io"),
        new FundifyUser("2f3e3e3e3e3e3e3e3e3e3e3e", "Lena", null, "foo2@quarkus.io"),
        new FundifyUser("3f3e3e3e3e3e3e3e3e3e3e3e", "Tim", null, "foo3@quarkus.io"),
        new FundifyUser("4f3e3e3e3e3e3e3e3e3e3e3e", "Emma", null, "foo4@quarkus.io"),
        new FundifyUser("5f3e3e3e3e3e3e3e3e3e3e3e", "Vincent", null, "foo5@quarkus.io"),
        new FundifyUser("6f3e3e3e3e3e3e3e3e3e3e3e", "Julia", null, "foo6@quarkus.io")
    );
    existingCall.persistOrUpdate();

    CallMongoEntity existingCallManagedBySync = new CallMongoEntity();
    existingCallManagedBySync.id = new ObjectId(callIdManagedBySync);
    existingCallManagedBySync.funderId = new ObjectId(funderId);
    existingCallManagedBySync.status = EPublicationStatus.PUBLISHED;
    existingCallManagedBySync.entryOrigin = EEntryOrigin.ENDPOINT;
    existingCallManagedBySync.persistOrUpdate();

    CallMongoEntity existingExternalCall = new CallMongoEntity();
    existingExternalCall.id = new ObjectId(callIdExternal);
    existingExternalCall.funderId = new ObjectId(externalFunderId);
    existingExternalCall.status = EPublicationStatus.DRAFT;
    existingExternalCall.ownerKind = CallOwner.CallOwnerKind.UNIVERSITY.toString();
    existingExternalCall.ownerId = universityId;
    existingExternalCall.persistOrUpdate();
  }

  @Test
  @WithFFGFunderUser
  void givenCallWithStatusDraft_whenAdd_thenReturnSameCallWithId() {
    final CallWebModel requestCall = generateTestCall(
        null,
        null,
        FFG_FUNDER_AFFILIATION,
        funderId,
        EPublicationStatusWebModel.DRAFT);

    CallWebModel responseCall = given()
        .contentType(ContentType.JSON)
        .body(requestCall)
        .when()
        .post(ADD_ENTITY)
        .then()
        .statusCode(200)
        .extract()
        .as(CallWebModel.class);

    Assertions.assertNotNull(responseCall.id());
    Assertions.assertNull(responseCall.risId());
    Assertions.assertEquals(requestCall.acronym(), responseCall.acronym());
  }

  @Test
  @WithFFGFunderUser
  void givenCallWithStatusDraft_whenAdd_thenNoRegistrationDateAndLastSync() {
    final CallWebModel requestCall = generateTestCall(
        null,
        null,
        FFG_FUNDER_AFFILIATION,
        funderId,
        EPublicationStatusWebModel.DRAFT);

    CallWebModel responseCall = given()
        .contentType(ContentType.JSON)
        .body(requestCall)
        .when()
        .post(ADD_ENTITY)
        .then()
        .statusCode(200)
        .extract()
        .as(CallWebModel.class);

    CallMongoEntity result = CallMongoEntity.findById(new ObjectId(responseCall.id()));

    Assertions.assertNull(result.registrationDate);
    Assertions.assertNull(result.lastSync);
  }


  @Test
  @WithFFGFunderUser
  void givenCallWithStatusPublished_whenAdd_thenReturnSameCallWithRisId() {
    final CallWebModel requestCall = generateTestCall(
        null,
        null,
        FFG_FUNDER_AFFILIATION,
        funderId,
        EPublicationStatusWebModel.PUBLISHED);

    CallWebModel responseCall = given()
        .contentType(ContentType.JSON)
        .body(requestCall)
        .when()
        .post(ADD_ENTITY)
        .then()
        .statusCode(200)
        .extract()
        .as(CallWebModel.class);

    Assertions.assertNotNull(responseCall.id());
    Assertions.assertNotNull(responseCall.risId());
    Assertions.assertEquals(requestCall.acronym(), responseCall.acronym());
  }

  @Test
  @WithFFGFunderUser
  void givenCallWithStatusPublished_whenAdd_thenAssignRegisteredDateAndLastSync() {
    final CallWebModel requestCall = generateTestCall(
        null,
        null,
        FFG_FUNDER_AFFILIATION,
        funderId,
        EPublicationStatusWebModel.PUBLISHED);

    CallWebModel responseCall = given()
        .contentType(ContentType.JSON)
        .body(requestCall)
        .when()
        .post(ADD_ENTITY)
        .then()
        .statusCode(200)
        .extract()
        .as(CallWebModel.class);

    CallMongoEntity result = CallMongoEntity.findById(new ObjectId(responseCall.id()));

    Assertions.assertNotNull(result.registrationDate);
    Assertions.assertNotNull(result.lastSync);
    Assertions.assertEquals(result.registrationDate, result.lastSync);
  }

  @Test
  @WithFFGFunderUser
  void givenPublishedCallWithRisId_whenAdd_thenReturnBadRequest() {

    final CallWebModel requestCall = generateTestCall(
        null,
        "ris:TEST:funding:call1",
        FFG_FUNDER_AFFILIATION,
        funderId,
        EPublicationStatusWebModel.PUBLISHED);

    given()
        .contentType(ContentType.JSON)
        .body(requestCall)
        .when()
        .post(ADD_ENTITY)
        .then()
        .statusCode(400);
  }

  @Test
  @WithTUWUser
  void givenPublishedCallWithNoSubscription_whenSubscribe_thenReturnCallWithSubscription() {

    CallMongoEntity existingCall = new CallMongoEntity();
    existingCall.id = new ObjectId(callId);
    existingCall.funderId = new ObjectId(funderId);
    existingCall.status = EPublicationStatus.PUBLISHED;
    existingCall.subscriptions = new ArrayList<>();
    existingCall.update();

    CallWebModel responseCall = given()
        .contentType(ContentType.JSON)
        .body(SUBSCRIBED)
        .when()
        .patch(ENTITY_UPDATE_SUBSCRIPTIONS, existingCall.id.toHexString())
        .then()
        .statusCode(200)
        .extract()
        .as(CallWebModel.class);

     Assertions.assertEquals(ESubscriptionStatusWebModel.SUBSCRIBED, responseCall.subscriptionStatus());
  }

  @Test
  @WithTUWUser
  void givenPublishedCallWithNoSubscriptionField_whenSubscribe_thenReturnCallWithSubscription() {

    CallMongoEntity existingCall = new CallMongoEntity();
    existingCall.id = new ObjectId(callId);
    existingCall.funderId = new ObjectId(funderId);
    existingCall.status = EPublicationStatus.PUBLISHED;
    existingCall.update();

    CallWebModel responseCall = given()
        .contentType(ContentType.JSON)
        .body(SUBSCRIBED)
        .when()
        .patch(ENTITY_UPDATE_SUBSCRIPTIONS, existingCall.id.toHexString())
        .then()
        .statusCode(200)
        .extract()
        .as(CallWebModel.class);

    Assertions.assertEquals(ESubscriptionStatusWebModel.SUBSCRIBED, responseCall.subscriptionStatus());
  }

  @Test
  @WithFFGFunderUser
  void givenDraftCall_whenUpdate_thenReturnUpdatedCall() {

    final CallWebModel requestCall = generateTestCall(
        callId,
        null,
        FFG_FUNDER_AFFILIATION,
        funderId,
        EPublicationStatusWebModel.DRAFT);

    CallWebModel responseCall = given()
        .contentType(ContentType.JSON)
        .body(requestCall)
        .when()
        .put(UPDATE_ENTITY)
        .then()
        .statusCode(200)
        .extract()
        .as(CallWebModel.class);

    Assertions.assertEquals(requestCall.id(), responseCall.id());
    Assertions.assertEquals(requestCall.acronym(), responseCall.acronym());
  }

  @Test
  @WithFFGFunderUser
  void givenPublishedCall_whenUpdate_thenUpdateLastSync() {
    CallMongoEntity existingCall = new CallMongoEntity();
    existingCall.id = new ObjectId(callId);
    existingCall.funderId = new ObjectId(funderId);
    existingCall.status = EPublicationStatus.PUBLISHED;
    existingCall.subscriptions = new ArrayList<>();
    existingCall.ownerKind = CallOwner.CallOwnerKind.FUNDER.toString();
    existingCall.ownerId = funderId;
    existingCall.update();

    LocalDateTime controlDate = LocalDateTime.now();

    CallWebModel requestCall = generateTestCall(
        callId,
        null,
        FFG_FUNDER_AFFILIATION,
        funderId,
        EPublicationStatusWebModel.PUBLISHED);

    CallWebModel responseCall = given()
        .contentType(ContentType.JSON)
        .body(requestCall)
        .when()
        .put(UPDATE_ENTITY)
        .then()
        .statusCode(200)
        .extract()
        .as(CallWebModel.class);

    Assertions.assertTrue(controlDate.isBefore(responseCall.lastSync()));
  }

  @Test
  @WithFFGFunderUser
  void whenAddCall_thenSetLastUpdatedAt() {
      final CallWebModel requestCall = generateTestCall(
              null,
              null,
              FFG_FUNDER_AFFILIATION,
              funderId,
              EPublicationStatusWebModel.DRAFT);

      LocalDateTime dateBeforeAdd = LocalDateTime.now();
      CallWebModel responseCall = given()
              .contentType(ContentType.JSON)
              .body(requestCall)
              .when()
              .post(ADD_ENTITY)
              .then()
              .statusCode(200)
              .extract()
              .as(CallWebModel.class);
      LocalDateTime dateAfterAdd = LocalDateTime.now();

      Assertions.assertTrue(dateBeforeAdd.isBefore(responseCall.lastUpdatedAt()));
      Assertions.assertTrue(dateAfterAdd.isAfter(responseCall.lastUpdatedAt()));
  }

  @Test
  @WithFFGFunderUser
  void whenUpdateCall_thenUpdateLastUpdatedBy() {
      LocalDateTime dateBeforeUpdate = LocalDateTime.now();
      CallWebModel requestCall = generateTestCall(
              callId,
              null,
              FFG_FUNDER_AFFILIATION,
              funderId,
              EPublicationStatusWebModel.PUBLISHED
      );

      CallWebModel responseCall = given()
                .contentType(ContentType.JSON)
                .body(requestCall)
                .when()
                .put(UPDATE_ENTITY)
                .then()
                .statusCode(200)
                .extract()
                .as(CallWebModel.class);

      LocalDateTime dateAfterUpdate = LocalDateTime.now();

      Assertions.assertTrue(dateBeforeUpdate.isBefore(responseCall.lastSync()));
      Assertions.assertTrue(dateAfterUpdate.isAfter(responseCall.lastSync()));
  }

  @Test
  @WithFFGFunderUser
  void whenProcessingPendingEmails_thenCorrectNumberOfMailsAreSent() {

    CallWebModel requestCall = generateTestCall(
        callId,
        null,
        FFG_FUNDER_AFFILIATION,
        funderId,
        EPublicationStatusWebModel.PUBLISHED
    );

    given()
        .contentType(ContentType.JSON)
        .body(requestCall)
        .when()
        .put(UPDATE_ENTITY)
        .then()
        .statusCode(200)
        .extract()
        .as(CallWebModel.class);

    emailService.processPendingEmails();

    List<Mail> sentToSpecificUser = mailbox.getMailsSentTo("foo1@quarkus.io");
    Mail actual = sentToSpecificUser.getFirst();
    Assertions.assertTrue(actual.getHtml().contains("Hello Chris"));
    Assertions.assertEquals(BATCH_SIZE, mailbox.getTotalMessagesSent());
  }

  @Test
  @WithFFGFunderUser
  void givenUnknownCall_whenUpdate_thenReturnNotFound() {

    final String validId = "5f3e3e3e3e3e3e3e3e3e3e3e";
    final CallWebModel requestCall = generateTestCall(
        validId,
        null,
        FFG_FUNDER_AFFILIATION,
        funderId,
        EPublicationStatusWebModel.DRAFT);

    given()
        .contentType(ContentType.JSON)
        .body(requestCall)
        .when()
        .put(UPDATE_ENTITY)
        .then()
        .statusCode(404);
  }

  @Test
  @WithFFGFunderUser
  void givenCallId_whenDelete_thenCallIsDeleted() {

    given()
        .when()
        .delete(DELETE_ENTITY_BY_ID_REPLACE_PARAMTER, callId)
        .then()
        .statusCode(204);

    Assertions.assertNull(CallMongoEntity.findById(new CallId(callId)));
  }

  @Test
  @WithFFGFunderUser
  void givenStatus_whenListAll_thenReturnCallsWithStatus() {
    CallMongoEntity.deleteAll();
    FunderMongoEntity funder = new FunderMongoEntity();
    funder.persist();

    CallMongoEntity existingCall1 = new CallMongoEntity();
    existingCall1.status = EPublicationStatus.PUBLISHED;
    existingCall1.funderId = funder.id;
    existingCall1.subscriptions = new ArrayList<>();
    existingCall1.persist();

    CallMongoEntity existingCall2 = new CallMongoEntity();
    existingCall2.status = EPublicationStatus.DRAFT;
    existingCall2.funderId = funder.id;
    existingCall2.subscriptions = new ArrayList<>();
    existingCall2.persist();

    CallWebModel[] calls = given()
        .when()
        .queryParam(QUERY_PARAM_STATUS, EPublicationStatus.PUBLISHED)
        .get(ENTITY_LIST)
        .then()
        .statusCode(200)
        .extract()
        .as(CallWebModel[].class);

    Assertions.assertEquals(1, calls.length);
    Assertions.assertEquals(EPublicationStatusWebModel.PUBLISHED, calls[0].status());
  }

  @Test
  @WithFFGFunderUser
  void givenId_whenGetById_thenReturnCallWithId() {
    CallMongoEntity existingCall = new CallMongoEntity();
    existingCall.subscriptions = new ArrayList<>();
    existingCall.persist();

    CallWebModel call = given()
        .when()
        .get(ENTITY_BY_ID_REPLACE_PARAMETER, existingCall.id.toHexString())
        .then()
        .statusCode(200)
        .extract()
        .as(CallWebModel.class);

    Assertions.assertEquals(existingCall.id.toHexString(), call.id());
  }

  @Test
  @WithFFGFunderUser
  void givenUnknownId_whenGetById_thenReturnNotFound() {
    final String unknownId = "5f3e3e3e3e3e3e3e3e3e3e3e";

    given()
        .when()
        .get(ENTITY_BY_ID_REPLACE_PARAMETER, unknownId)
        .then()
        .statusCode(404);
  }

  @Test
  @WithFFGFunderUser
  void givenInvalidId_whenGetById_thenReturnBadRequest() {
    final String invalidId = "invalidId";

    given()
        .when()
        .get(ENTITY_BY_ID_REPLACE_PARAMETER, invalidId)
        .then()
        .statusCode(404);
  }

  @Test
  @WithTUWUser
  void whenAddExternalCall_thenSetExternalCallOwnedBy() {
      final CallWebModel requestCall = generateTestCall(
              null,
              null,
              XYZ_FUNDER_AFFILIATION,
              externalFunderId,
              EPublicationStatusWebModel.DRAFT);

      CallWebModel responseCall = given()
              .contentType(ContentType.JSON)
              .body(requestCall)
              .when()
              .post(ADD_ENTITY)
              .then()
              .statusCode(200)
              .extract()
              .as(CallWebModel.class);

      Assertions.assertEquals(TU_WIEN_UNIVERSITY_AFFILIATION, responseCall.callOwner().acronym());
      Assertions.assertEquals(CallOwner.CallOwnerKind.UNIVERSITY.toString(), responseCall.callOwner().kind());
      Assertions.assertEquals(universityId, responseCall.callOwner().id());
  }

  @Test
  @WithTUWUser
  void when_Update_ExternalCall_AsCallOwner_then_ReturnUpdatedCall() {

      CallWebModel requestCall = generateTestCall(
              callIdExternal,
              null,
              XYZ_FUNDER_AFFILIATION,
              externalFunderId,
              EPublicationStatusWebModel.PUBLISHED
      );

      CallWebModel responseCall = given()
              .contentType(ContentType.JSON)
              .body(requestCall)
              .when()
              .put(UPDATE_ENTITY)
              .then()
              .statusCode(200)
              .extract()
              .as(CallWebModel.class);

      Assertions.assertEquals(requestCall.id(), responseCall.id());
      Assertions.assertEquals(TU_WIEN_UNIVERSITY_AFFILIATION, responseCall.callOwner().acronym());
  }

  @Test
  @WithTUWUser
  void when_Delete_ExternalCall_AsCallOwner_then_CallIsDeleted() {
      given()
        .when()
        .delete(DELETE_ENTITY_BY_ID_REPLACE_PARAMTER, callIdExternal)
        .then()
        .statusCode(204);

      Assertions.assertNull(CallMongoEntity.findById(new ObjectId(callIdExternal)));
  }

  @Nested
  @QuarkusTest
  @TestHTTPEndpoint(CallResource.class)
  class PermissionTests {

    @Test
    @WithXYZFunderUser
    void when_Add_CallAsUnaffiliatedFunder_then_returnForbidden() {
      final CallWebModel requestCall = generateTestCall(
          null,
          null,
          FFG_FUNDER_AFFILIATION,
          funderId,
          EPublicationStatusWebModel.DRAFT);

      given()
          .contentType(ContentType.JSON)
          .body(requestCall)
          .when()
          .post(ADD_ENTITY)
          .then()
          .statusCode(403);
    }

    @Test
    @WithAdminUser
    void when_Add_CallAsAdmin_thenReturnOk() {
      final CallWebModel requestCall = generateTestCall(
          null,
          null,
          FFG_FUNDER_AFFILIATION,
          funderId,
          EPublicationStatusWebModel.PUBLISHED);

      given()
          .contentType(ContentType.JSON)
          .body(requestCall)
          .when()
          .post(ADD_ENTITY)
          .then()
          .statusCode(200);
    }


    @Test
    @WithXYZFunderUser
    void when_Update_DraftCallAsUnaffiliatedUser_then_ReturnForbidden() {

      final CallWebModel requestCall = generateTestCall(
          callId,
          null,
          FFG_FUNDER_AFFILIATION,
          funderId,
          EPublicationStatusWebModel.DRAFT);

      given()
          .contentType(ContentType.JSON)
          .body(requestCall)
          .when()
          .put(UPDATE_ENTITY)
          .then()
          .statusCode(403);
    }
    @Test
    @WithAdminUser
    void when_Update_CallManagedThroughSync_then_ReturnForbidden() {

      final CallWebModel requestCall = generateTestCall(
          callIdManagedBySync,
          null,
          FFG_FUNDER_AFFILIATION,
          funderId,
          EPublicationStatusWebModel.DRAFT);

      given()
          .contentType(ContentType.JSON)
          .body(requestCall)
          .when()
          .put(UPDATE_ENTITY)
          .then()
          .statusCode(403);
    }


    @Test
    @WithXYZFunderUser
    void when_Delete_CallIdAsUnaffiliatedUser_then_ReturnForbidden() {
      given()
          .when()
          .delete(DELETE_ENTITY_BY_ID_REPLACE_PARAMTER, callId)
          .then()
          .statusCode(403);
    }

    @Test
    @WithAdminUser
    void when_Delete_CallManagedThroughSync_then_ReturnForbidden() {
      given()
          .when()
          .delete(DELETE_ENTITY_BY_ID_REPLACE_PARAMTER, callIdManagedBySync)
          .then()
          .statusCode(403);
    }
  }

  private CallWebModel generateTestCall(
      String id,
      String risId,
      String acronym,
      String funderId,
      EPublicationStatusWebModel status) {

    return new CallWebModel(
        id,
        status,
        ECallTypeWebModel.CALL,
        null,
        null,
        null,
        null,
        risId,
        List.of(new TranslatedTextWebModel("FFG", ELanguageWebModel.GERMAN, ETranslationWebModel.ORIGINAL)),
        List.of(ETargetGroupWebModel.UNIVERSITY),
        List.of(new StandardizedSubjectWebModel(1, "1", "NATURAL SCIENCES")),
        List.of((EFundingCharacteristicWebModel.SCIENTIFIC_PROGRAMME)),
        EFundingSchemeWebModel.GRANT,
        ELegalTypeWebModel.PROJECT26,
        new MonetaryNumberWebModel(new BigDecimal(10000), ECurrencyWebModel.EUR),
        new MonetaryNumberWebModel(new BigDecimal(20000), ECurrencyWebModel.EUR),
        EAnswerYNWebModel.YES,
        new BigDecimal(5),
        new BigDecimal(6),
        new TimeSpanWebModel(0,0,1),
        new TimeSpanWebModel(0,0,3),
        null,
        List.of(ELanguageWebModel.ENGLISH),
        List.of(EModeOfSubmissionWebModel.ONLINE_FULL),
        List.of(new FunderContactWebModel("name", "email@test.at", "phone")),
        new FunderRefWebModel(funderId, null, null, null, List.of(
                new TranslatedTextWebModel(acronym, ELanguageWebModel.GERMAN, ETranslationWebModel.ORIGINAL)), acronym, null),
        null,
        acronym,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );
  }
}