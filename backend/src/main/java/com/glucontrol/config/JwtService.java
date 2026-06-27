package com.glucontrol.config;

import com.glucontrol.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {
  private final SecretKey key; private final long expirationHours;
  public JwtService(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.expiration-hours}") long expirationHours){
    if(secret.length()<32) throw new IllegalArgumentException("APP_JWT_SECRET debe tener al menos 32 caracteres");
    this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));this.expirationHours=expirationHours;
  }
  public String issue(User user){Instant now=Instant.now();return Jwts.builder().subject(user.getEmail()).claim("role",user.getRole().name()).claim("userId",user.getId()).issuedAt(Date.from(now)).expiration(Date.from(now.plus(expirationHours,ChronoUnit.HOURS))).signWith(key).compact();}
  public Claims parse(String token){return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();}
}
