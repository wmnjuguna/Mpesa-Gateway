package io.github.wmjuguna.daraja.config;

import java.net.MalformedURLException;
import java.net.URI;
import java.text.ParseException;
import java.util.List;
import java.util.Set;

import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
public class SecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {
            "/mobile/stk",
            "/mobile/confirm/payment",
            "/mobile/validate/payment",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/error"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder(
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri,
            @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri,
            @Value("${security.oauth2.required-audience}") String requiredAudience
    ) throws MalformedURLException {
        if (requiredAudience == null || requiredAudience.isBlank()) {
            throw new IllegalStateException("security.oauth2.required-audience must be configured");
        }

        // Create JWK source from Keycloak's JWKS endpoint with caching
        JWKSource<SecurityContext> jwkSource = JWKSourceBuilder
                .create(URI.create(jwkSetUri).toURL())
                .build();

        // Explicitly allow asymmetric signature algorithms including EdDSA (OKP keys).
        JWSVerificationKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(
                Set.of(
                        JWSAlgorithm.RS256,
                        JWSAlgorithm.RS384,
                        JWSAlgorithm.RS512,
                        JWSAlgorithm.PS256,
                        JWSAlgorithm.PS384,
                        JWSAlgorithm.PS512,
                        JWSAlgorithm.ES256,
                        JWSAlgorithm.ES384,
                        JWSAlgorithm.ES512,
                        JWSAlgorithm.EdDSA,
                        JWSAlgorithm.Ed25519,
                        JWSAlgorithm.Ed448
                ),
                jwkSource
        );

        DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWSKeySelector(keySelector);

        // Build decoder with custom processor
        NimbusJwtDecoder jwtDecoder = new NimbusJwtDecoder(jwtProcessor);

        // Add validators for issuer and audience
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
        OAuth2TokenValidator<Jwt> withAudience = token -> {
            List<String> audience = token.getAudience();
            if (audience != null && audience.contains(requiredAudience)) {
                return OAuth2TokenValidatorResult.success();
            }
            OAuth2Error error = new OAuth2Error(
                    "invalid_token",
                    "Token must include required audience: " + requiredAudience,
                    null
            );
            return OAuth2TokenValidatorResult.failure(error);
        };
        jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                withIssuer,
                withAudience
        ));
        return token -> {
            try {
                Jwt jwt = jwtDecoder.decode(token);
                if (log.isDebugEnabled()) {
                    log.debug(
                            "JWT accepted: alg={}, kid={}, iss={}, aud={}, exp={}, tokenLength={}",
                            jwt.getHeaders().get("alg"),
                            jwt.getHeaders().get("kid"),
                            jwt.getIssuer(),
                            jwt.getAudience(),
                            jwt.getExpiresAt(),
                            token != null ? token.length() : 0
                    );
                }
                return jwt;
            } catch (JwtException ex) {
                logJwtDecodeFailure(token, ex);
                throw ex;
            }
        };
    }

    private void logJwtDecodeFailure(String token, JwtException ex) {
        int tokenLength = token != null ? token.length() : 0;
        int tokenSegments = token == null || token.isBlank() ? 0 : token.split("\\.", -1).length;
        if (token == null || token.isBlank()) {
            log.error(
                    "JWT decode failed: message='{}', tokenMissing=true, tokenLength={}, tokenSegments={}",
                    ex.getMessage(),
                    tokenLength,
                    tokenSegments
            );
            return;
        }

        try {
            SignedJWT signedJwt = SignedJWT.parse(token);
            log.error(
                    "JWT decode failed: message='{}', alg='{}', kid='{}', iss='{}', aud='{}', tokenLength={}, tokenSegments={}",
                    ex.getMessage(),
                    signedJwt.getHeader().getAlgorithm(),
                    signedJwt.getHeader().getKeyID(),
                    signedJwt.getJWTClaimsSet().getIssuer(),
                    signedJwt.getJWTClaimsSet().getAudience(),
                    tokenLength,
                    tokenSegments
            );
        } catch (ParseException parseException) {
            log.error(
                    "JWT decode failed: message='{}', parseError='{}', tokenLength={}, tokenSegments={}",
                    ex.getMessage(),
                    parseException.getMessage(),
                    tokenLength,
                    tokenSegments
            );
        }
    }
}
