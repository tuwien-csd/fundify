package at.ac.tuwien.fundify.bootstrap.integration.jwt;

import at.ac.tuwien.fundify.adapters.in.rest.auth.JwtUserService;
import at.ac.tuwien.fundify.domain.common.UserRole;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
class JwtUserServiceTest {

  @Inject
  JwtUserService jwtUserService;

  @Test
  @TestSecurity(user = "testUser", roles = {UserRole.Names.ADMIN})
  @JwtSecurity(claims = {
      @Claim(key = "name", value = "John Doe"),
  })
  void given_validJwt_then_extractUserName() {
    Assertions.assertEquals("John Doe", jwtUserService.getCurrentUserName());
  }

  @Test
  @TestSecurity(user = "testUser", roles = {UserRole.Names.ADMIN})
  @JwtSecurity(claims = {
      @Claim(key = "sub", value = "user-123"),
  })
  void given_validJwt_then_extractUserId() {
    Assertions.assertEquals("user-123", jwtUserService.getCurrentUserId());
  }

  @Test
  @TestSecurity(user = "testUser", roles = {UserRole.Names.ADMIN})
  @JwtSecurity(claims = {
      @Claim(key = "sub", value = "user-123"),
      @Claim(key = "name", value = "John Doe"),
      @Claim(key = "affiliation_id", value = "aff-42")
  })
  void whenAuthenticatedAsAdmin_thenClaimsAndAdminRoleAreExposed() {
    Assertions.assertTrue(jwtUserService.isUserAdmin());
  }

  @Test
  @TestSecurity(user = "testUser", roles = {UserRole.Names.FUNDER})
  @JwtSecurity(claims = {
      @Claim(key = "sub", value = "user-999"),
      @Claim(key = "name", value = "Jane User")
  })
  void whenAuthenticatedWithoutAdminRole_thenIsUserAdminIsFalse() {
    Assertions.assertFalse(jwtUserService.isUserAdmin());
  }
}
