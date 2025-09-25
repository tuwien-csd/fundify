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
@TestSecurity(user = "testAdmin", roles = {UserRole.Names.ADMIN})
@JwtSecurity(claims = {
    @Claim(key = "name", value = "Test Admin"),
    @Claim(key = "sub", value = "test_admin_id"),
})
public @interface WithAdminUser {

}
