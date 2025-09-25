package at.ac.tuwien.refop.bootstrap.e2e;

import static io.restassured.RestAssured.given;

import at.ac.tuwien.refop.adapters.in.rest.dto.UserPermissionsUpdateWebModel;
import at.ac.tuwien.refop.adapters.in.rest.resources.UserResource;
import at.ac.tuwien.refop.adapters.out.persistence.mongo.user.UserPermissionMongoRepository;
import at.ac.tuwien.refop.bootstrap.utils.users.WithAdminUser;
import at.ac.tuwien.refop.bootstrap.utils.users.WithFFGFunderUser;
import at.ac.tuwien.refop.domain.common.UserPermissionHolder;
import at.ac.tuwien.refop.domain.common.UserRole;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestHTTPEndpoint(UserResource.class)
@TestSecurity(authorizationEnabled = false)
class UserTest {

  @Inject
  UserPermissionMongoRepository userPermissionRepository;


  @Inject
  @CacheName("user-permissions")
  Cache userPermissionsCache;

  @BeforeEach
  void setup() {
    // clear user-permission collection to ensure deterministic behavior for DB override tests
    userPermissionRepository.deleteAll();
    userPermissionsCache.invalidateAll().await().indefinitely();
  }

  @Test
  @WithAdminUser
  void givenAdminUser_whenGetMe_thenReturnAdminPermissions() {
    UserPermissionHolder me = given()
        .when()
        .get("/me")
        .then()
        .statusCode(200)
        .extract()
        .as(UserPermissionHolder.class);

    Assertions.assertEquals("test_admin_id", me.userId());
    Assertions.assertTrue(me.roles().contains(UserRole.ADMIN));
    Assertions.assertEquals("admin", me.affiliationId());
  }

  @Test
  @WithFFGFunderUser
  void givenFunderUser_whenGetMe_thenReturnFunderPermissions() {
    UserPermissionHolder me = given()
        .when()
        .get("/me")
        .then()
        .statusCode(200)
        .extract()
        .as(UserPermissionHolder.class);

    Assertions.assertEquals("test_ffg_funder_id", me.userId());
    Assertions.assertTrue(me.roles().contains(UserRole.FUNDER));
    Assertions.assertEquals("TEST_FFG", me.affiliationId());
  }

  @Test
  @WithFFGFunderUser
  void givenFunderUser_whenDbHasOverride_thenGetMeReflectsOverride() {
    // Seed DB override for the funder user (DB should take precedence over config)
    userPermissionRepository.upsert(new UserPermissionHolder(
        "test_ffg_funder_id",
        List.of(UserRole.FUNDER),
        "OVERRIDE_FFG"
    ));

    UserPermissionHolder me = given()
        .when()
        .get("/me")
        .then()
        .statusCode(200)
        .extract()
        .as(UserPermissionHolder.class);

    Assertions.assertEquals("test_ffg_funder_id", me.userId());
    Assertions.assertTrue(me.roles().contains(UserRole.FUNDER));
    Assertions.assertEquals("OVERRIDE_FFG", me.affiliationId());
  }

  @Nested
  @TestHTTPEndpoint(UserResource.class)
  class UpdatePermissions {

    @Test
    @WithAdminUser
    void givenAdmin_whenUpdateFunderPermissions_thenDbOverridesConfigAndGetMeReflectsChange() {
      String targetUserId = "test_ffg_funder_id"; // exists in %test config
      UserPermissionsUpdateWebModel updateRequest = new UserPermissionsUpdateWebModel(
          List.of(UserRole.FUNDER),
          "OVERRIDE_FFG");

      UserPermissionHolder updated = given()
          .contentType(ContentType.JSON)
          .body(updateRequest)
          .when()
          .put("/{id}/permissions", targetUserId)
          .then()
          .statusCode(200)
          .extract()
          .as(UserPermissionHolder.class);

      Assertions.assertEquals(targetUserId, updated.userId());
      Assertions.assertEquals("OVERRIDE_FFG", updated.affiliationId());
      Assertions.assertTrue(updated.roles().contains(UserRole.FUNDER));

      // Verify persisted state via repository (cannot change identity mid-test)
      var fromDb = userPermissionRepository.findByUserId(targetUserId).orElseThrow();
      Assertions.assertEquals("OVERRIDE_FFG", fromDb.affiliationId());
      Assertions.assertTrue(fromDb.roles().contains(UserRole.FUNDER));
    }

    @Test
    @WithFFGFunderUser
    void givenNonAdmin_whenUpdatePermissions_thenForbidden() {
      String targetUserId = "some_user";
      UserPermissionsUpdateWebModel updateRequest = new UserPermissionsUpdateWebModel(
          List.of(UserRole.ANNOTATOR),
          "some_aff");

      given()
          .contentType(ContentType.JSON)
          .body(updateRequest)
          .pathParam("id", targetUserId)
          .when()
          .put("/{id}/permissions")
          .then()
          .statusCode(403);
    }
  }
}
