package com.glucontrol.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class WebConfig {
  @Bean BCryptPasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
  @Bean OpenAPI openAPI() { return new OpenAPI().info(new Info().title("GluControl API").version("1.0.0").description("API clínica para seguimiento de glucosa, alimentación y medicación.")); }
  @Bean WebMvcConfigurer cors(@Value("${app.cors.allowed-origins}") String origins) {
    var allowed = Arrays.stream(origins.split(",")).map(String::trim).toArray(String[]::new);
    return new WebMvcConfigurer() {
      @Override public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**").allowedOrigins(allowed).allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS").allowedHeaders("*");
      }
    };
  }
}

