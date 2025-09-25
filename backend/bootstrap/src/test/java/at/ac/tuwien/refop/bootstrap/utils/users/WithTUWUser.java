package at.ac.tuwien.refop.bootstrap.utils.users;

import at.ac.tuwien.refop.domain.common.UserRole;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static at.ac.tuwien.refop.bootstrap.utils.TestConstants.TUW_USER_ID;
import static at.ac.tuwien.refop.bootstrap.utils.TestConstants.TUW_USER_NAME;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@TestSecurity(user = "testUser_tuw", roles = {UserRole.Names.ANNOTATOR})
@JwtSecurity(claims = {
    @Claim(key = "name", value = TUW_USER_NAME),
    @Claim(key = "sub", value = TUW_USER_ID),
})
public @interface WithTUWUser {
}
