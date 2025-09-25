package at.ac.tuwien.refop.bootstrap.utils.users;

import at.ac.tuwien.refop.domain.common.UserRole;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@TestSecurity(user = "testUser_univie", roles = {UserRole.Names.ANNOTATOR})
@JwtSecurity(claims = {
    @Claim(key = "name", value = "UniVie Uni"),
    @Claim(key = "sub", value = "test_univie_id"),
})
public @interface WithUniVieUser {
}
