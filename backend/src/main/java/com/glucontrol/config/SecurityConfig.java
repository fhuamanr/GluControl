package com.glucontrol.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration @RequiredArgsConstructor
public class SecurityConfig {
  private final JwtAuthenticationFilter jwtFilter;
  @Bean SecurityFilterChain security(HttpSecurity http)throws Exception{return http.csrf(csrf->csrf.disable()).cors(cors->{}).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(a->a.requestMatchers("/api/health","/api/auth/login","/api/auth/register","/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll().requestMatchers(HttpMethod.GET,"/api/uploads/**").permitAll().requestMatchers(HttpMethod.POST,"/api/uploads/**").hasRole("PATIENT").requestMatchers("/api/doctor/**").hasAnyRole("DOCTOR","ADMIN").requestMatchers(HttpMethod.POST,"/api/patients/**").hasRole("ADMIN").requestMatchers(HttpMethod.DELETE,"/api/patients/**").hasRole("ADMIN").anyRequest().authenticated()).addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class).build();}
}
