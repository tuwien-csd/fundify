package at.ac.tuwien.fundify.bootstrap.utils.users;

import at.ac.tuwien.fundify.domain.common.UserRole;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@TestSecurity(user = "testUser_tuw_api", roles = {UserRole.Names.EXTERNAL_API_CLIENT})
@JwtSecurity(claims = {
    @Claim(key = "name", value = "TUW Uni Api client"),
    @Claim(key = "sub", value = "test_tuw_api_client_id"),
})
public @interface WithTUWApiUser {
}
