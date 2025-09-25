package at.ac.tuwien.fundify.bootstrap.e2e;

import static at.ac.tuwien.fundify.bootstrap.utils.TestConstants.TU_WIEN_UNIVERSITY_AFFILIATION;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisAnnotatedCall;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisCall;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisFunding;
import at.ac.tuwien.fundify.adapters.common.ris.model.v1.RisIdentifierTypeEnum;
import at.ac.tuwien.fundify.adapters.in.rest.resources.RisFundingResource;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating.AnnotatedCallMongoEntity;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating.UniversityMongoEntity;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding.CallMongoEntity;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding.FunderMongoEntity;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.funding.ProgramMongoEntity;
import at.ac.tuwien.fundify.bootstrap.utils.users.WithAdminUser;
import at.ac.tuwien.fundify.bootstrap.utils.users.WithTUWApiUser;
import at.ac.tuwien.fundify.bootstrap.utils.users.WithUniVieApiUser;
import at.ac.tuwien.fundify.domain.common.DateRange;
import at.ac.tuwien.fundify.domain.common.EPublicationStatus;
import at.ac.tuwien.fundify.domain.common.ETargetGroup;
import at.ac.tuwien.fundify.domain.common.RisId;
import at.ac.tuwien.fundify.domain.funding.vo.CallStage;
import at.ac.tuwien.fundify.domain.funding.vo.Identifier;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EAustrianState;
import at.ac.tuwien.fundify.domain.funding.vo.enums.ECallType;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EIdentifierType;
import at.ac.tuwien.fundify.domain.funding.vo.enums.ERegionalScope;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestHTTPEndpoint(RisFundingResource.class)
@TestSecurity(authorizationEnabled = false)
class RisFundingTest {

    @BeforeEach
    void setUp() {
        CallMongoEntity.deleteAll();
        ProgramMongoEntity.deleteAll();
        AnnotatedCallMongoEntity.deleteAll();
        UniversityMongoEntity.deleteAll();
        FunderMongoEntity.deleteAll();
    }

    @Test
    void givenNoQueryParams_whenList_thenReturnsPublishedFundings() {
        ProgramMongoEntity program = new ProgramMongoEntity();
        program.status = EPublicationStatus.PUBLISHED;
        program.persist();
        CallMongoEntity call = new CallMongoEntity();
        call.status = EPublicationStatus.PUBLISHED;
        call.fundingType = ECallType.CALL;
        call.persist();
        CallMongoEntity callDraft = new CallMongoEntity();
        callDraft.status = EPublicationStatus.DRAFT;
        callDraft.persist();

        RisFunding[] result = given()
                .when()
                .get("/v1/fundings")
                .then()
                .statusCode(200)
                .extract().as(RisFunding[].class);

        Assertions.assertEquals(2, result.length);
    }

    @Test
    void givenNoQueryParams_whenList_thenReturnFundingsWithRisIdentifier() {
        ProgramMongoEntity program = new ProgramMongoEntity();
        program.status = EPublicationStatus.PUBLISHED;
        program.risId = RisId.generateFundingRisId("PROGRAM", "test").toString();
        program.identifiers = List.of(new Identifier(EIdentifierType.RIS_SYNERGY, program.risId));
        program.persist();
        CallMongoEntity call = new CallMongoEntity();
        call.status = EPublicationStatus.PUBLISHED;
        call.fundingType = ECallType.CALL;
        call.risId = RisId.generateFundingRisId("TEST", "test").toString();
        call.identifiers = List.of(new Identifier(EIdentifierType.RIS_SYNERGY, call.risId));
        call.persist();
        CallMongoEntity callDraft = new CallMongoEntity();
        callDraft.status = EPublicationStatus.DRAFT;
        callDraft.persist();

        RisFunding[] result = given()
                .when()
                .get("/v1/fundings")
                .then()
                .statusCode(200)
                .extract().as(RisFunding[].class);

        assertThat(result)
                .allMatch(funding -> funding.getIdentifiers().stream()
                        .anyMatch(identifier ->
                                identifier.getType() == RisIdentifierTypeEnum.RIS_SYNERGY &&
                                        identifier.getValue().equals(
                                                funding instanceof RisCall ? call.risId : program.risId
                                        )
                        )
                );
    }


    @Test
    void givenFundingTyp_whenList_thenReturnsFundingsWithGivenType() {
        ProgramMongoEntity program = new ProgramMongoEntity();
        program.status = EPublicationStatus.PUBLISHED;
        program.persist();
        CallMongoEntity call = new CallMongoEntity();
        call.status = EPublicationStatus.PUBLISHED;
        call.fundingType = ECallType.CALL;
        call.persist();

        RisFunding[] result = given()
                .queryParam("fundingType", "CALL")
                .when()
                .get("/v1/fundings")
                .then()
                .statusCode(200)
                .extract().as(RisFunding[].class);

        Assertions.assertEquals(1, result.length);
    }

    @Test
    void givenTargetGroup_whenList_thenReturnsFundingsWithGivenTargetGroup() {
        ProgramMongoEntity program = new ProgramMongoEntity();
        program.status = EPublicationStatus.PUBLISHED;
        program.persist();
        CallMongoEntity call = new CallMongoEntity();
        call.status = EPublicationStatus.PUBLISHED;
        call.fundingType = ECallType.CALL;
        call.targetGroups = List.of(ETargetGroup.RESEARCH_INSTITUTE);
        call.persist();

        RisFunding[] result = given()
                .queryParam("targetGroup", "RESEARCH_INSTITUTE")
                .when()
                .get("/v1/fundings")
                .then()
                .statusCode(200)
                .extract().as(RisFunding[].class);

        Assertions.assertEquals(1, result.length);
    }

    @Test
    void givenRunningCalls_whenList_thenReturnsFundingsThatAreRunningCalls() {
        ProgramMongoEntity program = new ProgramMongoEntity();
        program.status = EPublicationStatus.PUBLISHED;
        program.persist();
        CallMongoEntity call = new CallMongoEntity();
        call.status = EPublicationStatus.PUBLISHED;
        call.fundingType = ECallType.CALL;
        call.callStages = new ArrayList<>();
        call.callStages.add(new CallStage(
                1,
                new DateRange(
                        LocalDateTime.of(2000, 1, 1, 0, 0),
                        LocalDateTime.of(2222, 1, 1, 0, 0)
                ),
                List.of()
        ));
        call.persist();
        CallMongoEntity closedCall = new CallMongoEntity();
        closedCall.status = EPublicationStatus.PUBLISHED;
        closedCall.fundingType = ECallType.CALL;
        closedCall.callStages = new ArrayList<>();
        closedCall.callStages.add(new CallStage(
                1,
                new DateRange(
                        LocalDateTime.of(1900, 1, 1, 0, 0),
                        LocalDateTime.of(2000, 1, 1, 0, 0)
                ),
                List.of()
        ));
        closedCall.persist();

        RisFunding[] result = given()
                .queryParam("runningCalls", "true")
                .when()
                .get("/v1/fundings")
                .then()
                .statusCode(200)
                .extract().as(RisFunding[].class);

        Assertions.assertEquals(1, result.length);
    }

    @Test
    void givenRegion_whenList_thenReturnsFundingsWithGivenRegion() {
        ProgramMongoEntity program = new ProgramMongoEntity();
        program.status = EPublicationStatus.PUBLISHED;
        program.persist();
        CallMongoEntity call = new CallMongoEntity();
        call.status = EPublicationStatus.PUBLISHED;
        call.fundingType = ECallType.CALL;
        call.eligibleApplicantsRegions = List.of(EAustrianState.BURGENLAND);
        call.persist();

        RisFunding[] result = given()
                .queryParam("region", "BURGENLAND")
                .when()
                .get("/v1/fundings")
                .then()
                .statusCode(200)
                .extract().as(RisFunding[].class);

        Assertions.assertEquals(1, result.length);
    }

    @Test
    void givenFunderId_whenList_thenReturnsFundingsFromFunder() {
        FunderMongoEntity funder = new FunderMongoEntity();
        funder.persist();

        ProgramMongoEntity program = new ProgramMongoEntity();
        program.status = EPublicationStatus.PUBLISHED;
        program.funderId = funder.id;
        program.persist();

        CallMongoEntity call = new CallMongoEntity();
        call.status = EPublicationStatus.PUBLISHED;
        call.fundingType = ECallType.CALL;
        call.funderId = funder.id;
        call.persist();

        ProgramMongoEntity otherProgram = new ProgramMongoEntity();
        otherProgram.status = EPublicationStatus.PUBLISHED;
        otherProgram.persist();

        CallMongoEntity otherCall = new CallMongoEntity();
        otherCall.status = EPublicationStatus.PUBLISHED;
        otherCall.fundingType = ECallType.CALL;
        otherCall.persist();

        RisFunding[] result = given()
                .queryParam("funderId", funder.id.toHexString())
                .when()
                .get("/v1/fundings")
                .then()
                .statusCode(200)
                .extract().as(RisFunding[].class);

        Assertions.assertEquals(2, result.length);
    }

    @Test
    void givenApplicantsScope_whenList_thenReturnsFundingsWithGivenApplicantsScope() {
        ProgramMongoEntity program = new ProgramMongoEntity();
        program.status = EPublicationStatus.PUBLISHED;
        program.persist();
        CallMongoEntity nationalCall = new CallMongoEntity();
        nationalCall.status = EPublicationStatus.PUBLISHED;
        nationalCall.fundingType = ECallType.CALL;
        nationalCall.eligibleApplicantsScope = ERegionalScope.NATIONAL;
        nationalCall.persist();

        CallMongoEntity regionalCall = new CallMongoEntity();
        regionalCall.status = EPublicationStatus.PUBLISHED;
        regionalCall.fundingType = ECallType.CALL;
        regionalCall.eligibleApplicantsScope = ERegionalScope.REGIONAL;
        regionalCall.persist();

        RisFunding[] result = given()
                .queryParam("applicantsScope", "NATIONAL")
                .when()
                .get("/v1/fundings")
                .then()
                .statusCode(200)
                .extract().as(RisFunding[].class);

        Assertions.assertEquals(1, result.length);
    }

    @Test
    void givenRisId_whenGet_thenReturnProgram() {
        ProgramMongoEntity program = new ProgramMongoEntity();
        program.status = EPublicationStatus.PUBLISHED;
        program.acronym = "testProgram";
        program.persist();
        program.risId = RisId.generateFundingRisId("fwf", program.id.toHexString()).toString();
        program.update();

        RisFunding result = given()
                .pathParam("id", program.risId)
                .when()
                .get("/v1/fundings/{id}")
                .then()
                .statusCode(200)
                .extract().as(RisFunding.class);

        Assertions.assertEquals(program.acronym, result.getAcronym());
    }

    @Test
    void givenRisId_whenGet_thenReturnCallWithId() {
        CallMongoEntity call = new CallMongoEntity();
        call.status = EPublicationStatus.PUBLISHED;
        call.fundingType = ECallType.CALL;
        call.acronym = "testCall";
        call.persist();
        call.risId = RisId.generateFundingRisId("fwf", call.id.toHexString()).toString();
        call.update();

        RisFunding result = given()
                .pathParam("id", call.risId)
                .when()
                .get("/v1/fundings/{id}")
                .then()
                .statusCode(200)
                .extract().as(RisFunding.class);

        Assertions.assertEquals(call.acronym, result.getAcronym());
    }

    @Test
    void givenUnknownRisId_whenGet_thenReturnNotFound() {
        String validId = "5f3e3e3e3e3e3e3e3e3e3e3e";
        String validRisId = RisId.generateFundingRisId("fwf", validId).toString();
        given()
                .pathParam("id", validRisId)
                .when()
                .get("/v1/fundings/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    void givenInvalidFunderId_whenGet_thenReturnBadRequest() {
        String invalidId = "invalidId";
        given()
                .pathParam("id", invalidId)
                .when()
                .get("/v1/fundings/{id}")
                .then()
                .statusCode(400);
    }

    @Test
    void givenInvalidUniversityRisId_whenListAnnotatedCalls_thenReturnsBadRequest() {
        AnnotatedCallMongoEntity annotatedCall = new AnnotatedCallMongoEntity();
        annotatedCall.persist();

        given()
                .pathParam("universityRisId", "invalid-university-internalId")
                .when()
                .get("/v1/annotated-calls/university/{universityRisId}")
                .then()
                .statusCode(400);
    }

    @Test
    void givenUnknownUniversityRisId_whenListAnnotatedCalls_thenReturnsNotFound() {
        AnnotatedCallMongoEntity annotatedCall = new AnnotatedCallMongoEntity();
        annotatedCall.persist();

        given()
            .pathParam("universityRisId", RisId.generateOrgunitRisId("TEST", "123").toString())
            .when()
            .get("/v1/annotated-calls/university/{universityRisId}")
            .then()
            .statusCode(404);
    }

    @Test
    @WithTUWApiUser
    void givenCallRisIdAndUniversityRisId_whenGetAnnotatedCall_thenReturnAnnotatedCall() {
      UniversityMongoEntity university = insertAndGetUniversityEntity(
          TU_WIEN_UNIVERSITY_AFFILIATION);

      FunderMongoEntity funder = new FunderMongoEntity();
        funder.persist();

        ProgramMongoEntity program = new ProgramMongoEntity();
        program.funderId = funder.id;
        program.persist();

        RisId mockCallRisId = RisId.generateFundingRisId("test", "call");
        List<Identifier> mockCallIdentifiers = List.of(new Identifier(EIdentifierType.RIS_SYNERGY, mockCallRisId.toString()));

        CallMongoEntity call = new CallMongoEntity();
        call.fundingType = ECallType.CALL;
        call.acronym = "testCall";
        call.funderId = funder.id;
        call.partOfId = program.id;
        call.identifiers = mockCallIdentifiers;
        call.risId = mockCallRisId.toString();
        call.persist();

        AnnotatedCallMongoEntity annotatedCall = new AnnotatedCallMongoEntity();
        annotatedCall.universityId = university.id;
        annotatedCall.callId = call.id;
        annotatedCall.status = EPublicationStatus.PUBLISHED;
        annotatedCall.persist();

        RisAnnotatedCall result = given()
                .pathParam("universityRisId", university.risId)
                .pathParam("callRisId", mockCallRisId.toString())
                .when()
                .get("/v1/annotated-calls/university/{universityRisId}/call/{callRisId}")
                .then()
                .statusCode(200)
                .extract().as(RisAnnotatedCall.class);

        Assertions.assertEquals("testCall", result.getCall().getAcronym());
        Assertions.assertEquals(mockCallRisId.toString(), result.getCall().getIdentifiers().get(0).getValue());
    }

    @Test
    @WithTUWApiUser
    void givenValidUniversityRisId_whenListAnnotatedCalls_thenReturnsAnnotatedCalls() {
      UniversityMongoEntity university = insertSimpleAnnotatedCallFor(TU_WIEN_UNIVERSITY_AFFILIATION);

      RisAnnotatedCall[] result = given()
                .pathParam("universityRisId", university.risId)
                .when()
                .get("/v1/annotated-calls/university/{universityRisId}")
                .then()
                .statusCode(200)
                .extract().as(RisAnnotatedCall[].class);

        Assertions.assertEquals(1, result.length);
    }

    @Test
    @WithTUWApiUser
    void givenTargetGroup_whenListAnnotatedCalls_thenReturnsCallsWithGivenTargetGroup() {
        UniversityMongoEntity university = insertAndGetUniversityEntity(TU_WIEN_UNIVERSITY_AFFILIATION);

        FunderMongoEntity funder = new FunderMongoEntity();
        funder.persist();

        CallMongoEntity call = new CallMongoEntity();
        call.status = EPublicationStatus.PUBLISHED;
        call.fundingType = ECallType.CALL;
        call.targetGroups = List.of(ETargetGroup.RESEARCH_INSTITUTE);
        call.funderId = funder.id;
        call.persist();

        CallMongoEntity otherCall = new CallMongoEntity();
        otherCall.status = EPublicationStatus.PUBLISHED;
        otherCall.fundingType = ECallType.CALL;
        otherCall.targetGroups = List.of(ETargetGroup.COMPANY);
        otherCall.funderId = funder.id;
        otherCall.persist();

        AnnotatedCallMongoEntity annotatedCall = new AnnotatedCallMongoEntity();
        annotatedCall.universityId = university.id;
        annotatedCall.callId = call.id;
        annotatedCall.status = EPublicationStatus.PUBLISHED;
        annotatedCall.persist();

        AnnotatedCallMongoEntity otherAnnotatedCall = new AnnotatedCallMongoEntity();
        otherAnnotatedCall.universityId = university.id;
        otherAnnotatedCall.callId = otherCall.id;
        otherAnnotatedCall.status = EPublicationStatus.PUBLISHED;
        otherAnnotatedCall.persist();

        RisAnnotatedCall[] result = given()
                .pathParam("universityRisId", university.risId)
                .queryParam("targetGroup", "RESEARCH_INSTITUTE")
                .when()
                .get("/v1/annotated-calls/university/{universityRisId}")
                .then()
                .statusCode(200)
                .extract().as(RisAnnotatedCall[].class);

        Assertions.assertEquals(1, result.length);
        Assertions.assertEquals(ETargetGroup.RESEARCH_INSTITUTE.toString(),
                result[0].getCall().getTargetGroups().get(0).toString());
    }

    @Test
    @WithTUWApiUser
    void givenRunningCalls_whenListAnnotatedCalls_thenReturnsRunningCalls() {
        UniversityMongoEntity university = insertAndGetUniversityEntity(TU_WIEN_UNIVERSITY_AFFILIATION);

        FunderMongoEntity funder = new FunderMongoEntity();
        funder.persist();

        CallMongoEntity runningCall = new CallMongoEntity();
        runningCall.funderId = funder.id;
        runningCall.fundingType = ECallType.CALL;
        runningCall.status = EPublicationStatus.PUBLISHED;
        runningCall.callStages = new ArrayList<>();
        runningCall.callStages.add(new CallStage(
                1,
                new DateRange(
                        LocalDateTime.of(2000, 1, 1, 0, 0),
                        LocalDateTime.of(2222, 1, 1, 0, 0)
                ),
                List.of()
        ));
        runningCall.persist();

        CallMongoEntity closedCall = new CallMongoEntity();
        closedCall.status = EPublicationStatus.PUBLISHED;
        runningCall.fundingType = ECallType.CALL;
        closedCall.callStages = new ArrayList<>();
        closedCall.callStages.add(new CallStage(
                1,
                new DateRange(
                        LocalDateTime.of(1900, 1, 1, 0, 0),
                        LocalDateTime.of(2000, 1, 1, 0, 0)
                ),
                List.of()
        ));
        closedCall.persist();

        AnnotatedCallMongoEntity annotatedRunningCall = new AnnotatedCallMongoEntity();
        annotatedRunningCall.universityId = university.id;
        annotatedRunningCall.callId = runningCall.id;
        annotatedRunningCall.status = EPublicationStatus.PUBLISHED;
        annotatedRunningCall.persist();

        AnnotatedCallMongoEntity annotatedClosedCall = new AnnotatedCallMongoEntity();
        annotatedClosedCall.universityId = university.id;
        annotatedClosedCall.callId = closedCall.id;
        annotatedClosedCall.status = EPublicationStatus.PUBLISHED;
        annotatedClosedCall.persist();

        RisAnnotatedCall[] result = given()
                .pathParam("universityRisId", university.risId)
                .queryParam("runningCalls", "true")
                .when()
                .get("/v1/annotated-calls/university/{universityRisId}")
                .then()
                .statusCode(200)
                .extract().as(RisAnnotatedCall[].class);

        Assertions.assertEquals(1, result.length);
    }

    @Test
    @WithTUWApiUser
    void givenRegion_whenListAnnotatedCalls_thenReturnsCallsWithGivenRegion() {
      UniversityMongoEntity university = insertAndGetUniversityEntity(
          TU_WIEN_UNIVERSITY_AFFILIATION);

      CallMongoEntity call = new CallMongoEntity();
        call.status = EPublicationStatus.PUBLISHED;
        call.fundingType = ECallType.CALL;
        call.eligibleApplicantsRegions = List.of(EAustrianState.BURGENLAND);
        call.persist();

        CallMongoEntity otherCall = new CallMongoEntity();
        otherCall.status = EPublicationStatus.PUBLISHED;
        otherCall.fundingType = ECallType.CALL;
        otherCall.eligibleApplicantsRegions = List.of(EAustrianState.VIENNA);
        otherCall.persist();

        AnnotatedCallMongoEntity annotatedCall = new AnnotatedCallMongoEntity();
        annotatedCall.universityId = university.id;
        annotatedCall.callId = call.id;
        annotatedCall.status = EPublicationStatus.PUBLISHED;
        annotatedCall.persist();

        AnnotatedCallMongoEntity otherAnnotatedCall = new AnnotatedCallMongoEntity();
        otherAnnotatedCall.universityId = university.id;
        otherAnnotatedCall.callId = otherCall.id;
        otherAnnotatedCall.status = EPublicationStatus.PUBLISHED;
        otherAnnotatedCall.persist();

        RisAnnotatedCall[] result = given()
                .pathParam("universityRisId", university.risId)
                .queryParam("region", "BURGENLAND")
                .when()
                .get("/v1/annotated-calls/university/{universityRisId}")
                .then()
                .statusCode(200)
                .extract().as(RisAnnotatedCall[].class);

        Assertions.assertEquals(1, result.length);
    }

    @Test
    @WithTUWApiUser
    void givenFunderId_whenListAnnotatedCalls_thenReturnsCallsFromFunder() {
      UniversityMongoEntity university = insertAndGetUniversityEntity(
          TU_WIEN_UNIVERSITY_AFFILIATION);

      FunderMongoEntity funder = new FunderMongoEntity();
        funder.persist();

        CallMongoEntity call = new CallMongoEntity();
        call.status = EPublicationStatus.PUBLISHED;
        call.fundingType = ECallType.CALL;
        call.funderId = funder.id;
        call.persist();

        CallMongoEntity otherCall = new CallMongoEntity();
        otherCall.status = EPublicationStatus.PUBLISHED;
        call.fundingType = ECallType.CALL;
        otherCall.persist();

        AnnotatedCallMongoEntity annotatedCall = new AnnotatedCallMongoEntity();
        annotatedCall.universityId = university.id;
        annotatedCall.callId = call.id;
        annotatedCall.status = EPublicationStatus.PUBLISHED;
        annotatedCall.persist();

        AnnotatedCallMongoEntity otherAnnotatedCall = new AnnotatedCallMongoEntity();
        otherAnnotatedCall.universityId = university.id;
        otherAnnotatedCall.callId = otherCall.id;
        otherAnnotatedCall.status = EPublicationStatus.PUBLISHED;
        otherAnnotatedCall.persist();

        RisAnnotatedCall[] result = given()
                .pathParam("universityRisId", university.risId)
                .queryParam("funderId", funder.id.toHexString())
                .when()
                .get("/v1/annotated-calls/university/{universityRisId}")
                .then()
                .statusCode(200)
                .extract().as(RisAnnotatedCall[].class);

        Assertions.assertEquals(1, result.length);
    }

    @Test
    @WithTUWApiUser
    void givenApplicantsScope_whenListAnnotatedCalls_thenReturnsCallsWithGivenScope() {
      UniversityMongoEntity university = insertAndGetUniversityEntity(
          TU_WIEN_UNIVERSITY_AFFILIATION);

      CallMongoEntity nationalCall = new CallMongoEntity();
        nationalCall.status = EPublicationStatus.PUBLISHED;
        nationalCall.fundingType = ECallType.CALL;
        nationalCall.eligibleApplicantsScope = ERegionalScope.NATIONAL;
        nationalCall.persist();

        CallMongoEntity regionalCall = new CallMongoEntity();
        regionalCall.status = EPublicationStatus.PUBLISHED;
        regionalCall.fundingType = ECallType.CALL;
        regionalCall.eligibleApplicantsScope = ERegionalScope.REGIONAL;
        regionalCall.persist();

        AnnotatedCallMongoEntity annotatedNationalCall = new AnnotatedCallMongoEntity();
        annotatedNationalCall.universityId = university.id;
        annotatedNationalCall.callId = nationalCall.id;
        annotatedNationalCall.status = EPublicationStatus.PUBLISHED;
        annotatedNationalCall.persist();

        AnnotatedCallMongoEntity annotatedRegionalCall = new AnnotatedCallMongoEntity();
        annotatedRegionalCall.universityId = university.id;
        annotatedRegionalCall.callId = regionalCall.id;
        annotatedRegionalCall.status = EPublicationStatus.PUBLISHED;
        annotatedRegionalCall.persist();

        RisAnnotatedCall[] result = given()
                .pathParam("universityRisId", university.risId)
                .queryParam("applicantsScope", "NATIONAL")
                .when()
                .get("/v1/annotated-calls/university/{universityRisId}")
                .then()
                .statusCode(200)
                .extract().as(RisAnnotatedCall[].class);

        Assertions.assertEquals(1, result.length);
    }

  private static UniversityMongoEntity insertAndGetUniversityEntity(String acronym) {
    UniversityMongoEntity university = new UniversityMongoEntity();
    university.risId = RisId.generateOrgunitRisId("TEST", "UNI").toString();
    university.acronym = acronym;
    university.persist();
    return university;
  }

  @Test
  @WithTUWApiUser
    void givenMultipleFilters_whenListAnnotatedCalls_thenReturnsFilteredCalls() {
        UniversityMongoEntity university = insertAndGetUniversityEntity(TU_WIEN_UNIVERSITY_AFFILIATION);

        FunderMongoEntity funder = new FunderMongoEntity();
        funder.persist();

        CallMongoEntity matchingCall = new CallMongoEntity();
        matchingCall.status = EPublicationStatus.PUBLISHED;
        matchingCall.funderId = funder.id;
        matchingCall.fundingType = ECallType.CALL;
        matchingCall.targetGroups = List.of(ETargetGroup.RESEARCH_INSTITUTE);
        matchingCall.persist();

        CallMongoEntity nonMatchingCall = new CallMongoEntity();
        nonMatchingCall.status = EPublicationStatus.PUBLISHED;
        nonMatchingCall.fundingType = ECallType.ONGOING_CALL;
        nonMatchingCall.targetGroups = List.of(ETargetGroup.COMPANY);
        nonMatchingCall.persist();

        AnnotatedCallMongoEntity annotatedMatchingCall = new AnnotatedCallMongoEntity();
        annotatedMatchingCall.universityId = university.id;
        annotatedMatchingCall.callId = matchingCall.id;
        annotatedMatchingCall.status = EPublicationStatus.PUBLISHED;
        annotatedMatchingCall.persist();

        AnnotatedCallMongoEntity annotatedNonMatchingCall = new AnnotatedCallMongoEntity();
        annotatedNonMatchingCall.universityId = university.id;
        annotatedNonMatchingCall.callId = nonMatchingCall.id;
        annotatedNonMatchingCall.status = EPublicationStatus.PUBLISHED;
        annotatedNonMatchingCall.persist();

        RisAnnotatedCall[] result = given()
                .pathParam("universityRisId", university.risId)
                .queryParam("fundingType", "CALL")
                .queryParam("targetGroup", "RESEARCH_INSTITUTE")
                .queryParam("funderId", funder.id.toHexString())
                .when()
                .get("/v1/annotated-calls/university/{universityRisId}")
                .then()
                .statusCode(200)
                .extract().as(RisAnnotatedCall[].class);

        Assertions.assertEquals(1, result.length);
    }


  @Nested
  @QuarkusTest
  @TestHTTPEndpoint(RisFundingResource.class)
  class PermissionTests {

    @Test
    @WithUniVieApiUser
    void when_ListAnnotatedCalls_AsUnaffiliatedUser_Then_ReturnEmptyList() {
      UniversityMongoEntity university = insertSimpleAnnotatedCallFor(TU_WIEN_UNIVERSITY_AFFILIATION);

      RisAnnotatedCall[] result = given()
          .pathParam("universityRisId", university.risId)
          .when()
          .get("/v1/annotated-calls/university/{universityRisId}")
          .then()
          .statusCode(200)
          .extract().as(RisAnnotatedCall[].class);

      Assertions.assertEquals(0, result.length);
    }

    @Test
    @WithAdminUser
    void when_ListAnnotatedCalls_AsAdmin_Then_ReturnCalls() {
      UniversityMongoEntity university = insertSimpleAnnotatedCallFor(TU_WIEN_UNIVERSITY_AFFILIATION);

      RisAnnotatedCall[] result = given()
          .pathParam("universityRisId", university.risId)
          .when()
          .get("/v1/annotated-calls/university/{universityRisId}")
          .then()
          .statusCode(200)
          .extract().as(RisAnnotatedCall[].class);

      Assertions.assertEquals(1, result.length);
    }
  }

  private static UniversityMongoEntity insertSimpleAnnotatedCallFor(String universityAcronym) {
    UniversityMongoEntity university = insertAndGetUniversityEntity(
        universityAcronym);

    FunderMongoEntity funder = new FunderMongoEntity();
    funder.persist();

    ProgramMongoEntity program = new ProgramMongoEntity();
    program.funderId = funder.id;
    program.persist();

    CallMongoEntity call = new CallMongoEntity();
    call.fundingType = ECallType.CALL;
    call.funderId = funder.id;
    call.partOfId = program.id;
    call.status = EPublicationStatus.PUBLISHED;
    call.persist();

    AnnotatedCallMongoEntity annotatedCall = new AnnotatedCallMongoEntity();
    annotatedCall.universityId = university.id;
    annotatedCall.callId = call.id;
    annotatedCall.status = EPublicationStatus.PUBLISHED;
    annotatedCall.persist();
    return university;
  }

}