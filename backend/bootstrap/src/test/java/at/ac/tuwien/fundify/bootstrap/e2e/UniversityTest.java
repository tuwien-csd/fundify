package at.ac.tuwien.fundify.bootstrap.e2e;


import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.ENTITY_BY_ID_REPLACE_PARAMETER;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.ENTITY_LIST;
import static at.ac.tuwien.fundify.adapters.in.rest.constants.FundingEntityMethodPath.PATH_PARAM_ID;
import static io.restassured.RestAssured.given;

import at.ac.tuwien.fundify.adapters.in.rest.dto.UniversityWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.resources.UniversityResource;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.annotating.UniversityMongoEntity;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestHTTPEndpoint(UniversityResource.class)
@TestSecurity(authorizationEnabled = false)
class UniversityTest {

    @BeforeEach
    void setUp() {
        UniversityMongoEntity.deleteAll();
    }

    @Test
    void whenListUniversities_thenReturnsUniversitiesList() {
        // Setup data
        UniversityMongoEntity university1 = new UniversityMongoEntity();
        university1.acronym = "University A";
        university1.persist();

        UniversityMongoEntity university2 = new UniversityMongoEntity();
        university2.acronym = "University B";
        university2.persist();

        // Test listing universities
        List<UniversityWebModel> universities = given()
                .when()
                .get(ENTITY_LIST)
                .then()
                .statusCode(200)
                .extract()
                .body().jsonPath().getList(".", UniversityWebModel.class);

        Assertions.assertEquals(2, universities.size());
        Assertions.assertTrue(universities.stream().anyMatch(u -> u.acronym().equals("University A")));
        Assertions.assertTrue(universities.stream().anyMatch(u -> u.acronym().equals("University B")));
    }

    @Test
    void givenUniversityId_whenGetUniversity_thenReturnsUniversityDetails() {
        // Setup data
        UniversityMongoEntity university = new UniversityMongoEntity();
        university.acronym = "University C";
        university.persist();
        String universityId = university.id.toHexString();

        // Test getting specific university details
        UniversityWebModel responseUniversity = given()
                .pathParam(PATH_PARAM_ID, universityId)
                .when()
                .get(ENTITY_BY_ID_REPLACE_PARAMETER)
                .then()
                .statusCode(200)
                .extract()
                .as(UniversityWebModel.class);

        Assertions.assertNotNull(responseUniversity);
        Assertions.assertEquals("University C", responseUniversity.acronym());
    }

    @Test
    void givenInvalidUniversityId_whenGetUniversity_thenReturnsNotFound() {
        final String invalidId = new ObjectId().toHexString();

        // Test getting university with invalid ID
        given()
                .pathParam(PATH_PARAM_ID, invalidId)
                .when()
                .get(ENTITY_BY_ID_REPLACE_PARAMETER)
                .then()
                .statusCode(404);
    }
}