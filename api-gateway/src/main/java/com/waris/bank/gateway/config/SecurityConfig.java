package com.waris.bank.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
      @Value("${app.jwt.secret}") String secret) {

    // Public auth endpoints
    http.csrf(ServerHttpSecurity.CsrfSpec::disable);

    http.authorizeExchange(ex -> ex
        .pathMatchers("/api/v1/auth/**").permitAll()
        .pathMatchers("/actuator/**").permitAll()
        .anyExchange().authenticated()
    );

    SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    http.oauth2ResourceServer(oauth2 -> oauth2
        .jwt(jwt -> jwt.jwtDecoder(NimbusReactiveJwtDecoder.withSecretKey(key).build()))
    );

    return http.build();
  }
}
