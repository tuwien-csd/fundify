package at.ac.tuwien.fundify.bootstrap.e2e;

import static io.restassured.RestAssured.given;

import static org.mockito.Mockito.when;

import at.ac.tuwien.fundify.adapters.in.rest.dto.UserPermissionsUpdateWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.resources.UserResource;
import at.ac.tuwien.fundify.adapters.out.persistence.mongo.user.UserPermissionMongoRepository;
import at.ac.tuwien.fundify.application.port.out.keycloak.KeycloakUserRepository;
import at.ac.tuwien.fundify.bootstrap.utils.users.WithAdminUser;
import at.ac.tuwien.fundify.bootstrap.utils.users.WithFFGFunderUser;
import at.ac.tuwien.fundify.domain.common.KeycloakUser;
import at.ac.tuwien.fundify.domain.common.UserPermissionHolder;
import at.ac.tuwien.fundify.domain.common.UserRole;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.test.InjectMock;
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

  @InjectMock
  KeycloakUserRepository keycloakUserRepository;

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

  @Nested
  @TestHTTPEndpoint(UserResource.class)
  class GetAllUsers {

    @Test
    @WithAdminUser
    void givenAdmin_whenGetAllUsers_thenReturnsKeycloakUsers() {
      when(keycloakUserRepository.findAll()).thenReturn(List.of(
          new KeycloakUser("kc-1", "alice", "alice@example.com", "Alice", "A", true),
          new KeycloakUser("kc-2", "bob", "bob@example.com", "Bob", "B", false)));

      given()
          .when()
          .get()
          .then()
          .statusCode(200)
          .body("size()", org.hamcrest.Matchers.is(2))
          .body("[0].id", org.hamcrest.Matchers.is("kc-1"))
          .body("[0].email", org.hamcrest.Matchers.is("alice@example.com"))
          .body("[1].username", org.hamcrest.Matchers.is("bob"));
    }

    @Test
    @WithFFGFunderUser
    void givenNonAdmin_whenGetAllUsers_thenForbidden() {
      given()
          .when()
          .get()
          .then()
          .statusCode(403);
    }
  }

  @Nested
  @TestHTTPEndpoint(UserResource.class)
  class GetAllPermissions {

    @Test
    @WithAdminUser
    void givenAdmin_whenGetAllPermissions_thenReturnsPersistedPermissions() {
      userPermissionRepository.upsert(new UserPermissionHolder(
          "user-a", List.of(UserRole.ANNOTATOR), "univie"));
      userPermissionRepository.upsert(new UserPermissionHolder(
          "user-b", List.of(UserRole.FUNDER), "TEST_FFG"));

      UserPermissionHolder[] permissions = given()
          .when()
          .get("/permissions")
          .then()
          .statusCode(200)
          .extract()
          .as(UserPermissionHolder[].class);

      Assertions.assertEquals(2, permissions.length);
    }

    @Test
    @WithFFGFunderUser
    void givenNonAdmin_whenGetAllPermissions_thenForbidden() {
      given()
          .when()
          .get("/permissions")
          .then()
          .statusCode(403);
    }
  }

  @Nested
  @TestHTTPEndpoint(UserResource.class)
  class DeletePermissions {

    @Test
    @WithAdminUser
    void givenAdmin_whenDeletePermissions_thenRemovedFromDbAndReturnsNoContent() {
      String targetUserId = "user-to-delete";
      userPermissionRepository.upsert(new UserPermissionHolder(
          targetUserId, List.of(UserRole.ANNOTATOR), "univie"));

      given()
          .pathParam("id", targetUserId)
          .when()
          .delete("/{id}/permissions")
          .then()
          .statusCode(204);

      Assertions.assertTrue(userPermissionRepository.findByUserId(targetUserId).isEmpty());
    }

    @Test
    @WithAdminUser
    void givenAdmin_whenDeleteUnknownUser_thenStillReturnsNoContent() {
      given()
          .pathParam("id", "does-not-exist")
          .when()
          .delete("/{id}/permissions")
          .then()
          .statusCode(204);
    }

    @Test
    @WithFFGFunderUser
    void givenNonAdmin_whenDeletePermissions_thenForbidden() {
      given()
          .pathParam("id", "some_user")
          .when()
          .delete("/{id}/permissions")
          .then()
          .statusCode(403);
    }
  }
}
