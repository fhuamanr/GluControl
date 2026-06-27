package com.glucontrol;

import com.glucontrol.config.JwtService;
import com.glucontrol.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityFixtureTest {
  private static final String DEMO_HASH = "$2a$10$bgtmTLbbzQ3mU5Z0AeMkVeG5ab4SovuS3CARtJcAa4fbN58Dw.Mwq";

  @Test void demoPasswordMatchesSeed() {
    assertThat(new BCryptPasswordEncoder().matches("password", DEMO_HASH)).isTrue();
  }

  @Test void jwtCarriesIdentityAndRole() {
    User user = new User();
    user.setId(7L); user.setEmail("medico@glucontrol.pe"); user.setRole(User.Role.DOCTOR);
    JwtService jwt = new JwtService("test-secret-with-more-than-thirty-two-characters", 1);
    var claims = jwt.parse(jwt.issue(user));
    assertThat(claims.getSubject()).isEqualTo(user.getEmail());
    assertThat(claims.get("role", String.class)).isEqualTo("DOCTOR");
  }
}
