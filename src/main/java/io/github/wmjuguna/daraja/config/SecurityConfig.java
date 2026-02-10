package io.github.wmjuguna.daraja.config;

import java.net.MalformedURLException;
import java.net.URI;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
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
import org.springframework.security.oauth2.jwt.BadJwtException;
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
        OAuth2TokenValidator<Jwt> jwtValidator = new DelegatingOAuth2TokenValidator<>(
                withIssuer,
                withAudience
        );
        jwtDecoder.setJwtValidator(jwtValidator);
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
                try {
                    Jwt fallbackJwt = tryDecodeEdDsaToken(token, jwkSetUri, jwtValidator);
                    if (fallbackJwt != null) {
                        return fallbackJwt;
                    }
                } catch (JwtException fallbackException) {
                    logJwtDecodeFailure(token, fallbackException, jwkSetUri);
                    throw fallbackException;
                }
                logJwtDecodeFailure(token, ex, jwkSetUri);
                throw ex;
            }
        };
    }

    private Jwt tryDecodeEdDsaToken(String token, String jwkSetUri, OAuth2TokenValidator<Jwt> jwtValidator) {
        if (token == null || token.isBlank()) {
            return null;
        }

        SignedJWT signedJwt;
        try {
            signedJwt = SignedJWT.parse(token);
        } catch (ParseException ex) {
            return null;
        }

        JWSAlgorithm algorithm = signedJwt.getHeader().getAlgorithm();
        if (algorithm == null || !(JWSAlgorithm.EdDSA.equals(algorithm) || JWSAlgorithm.Ed25519.equals(algorithm))) {
            return null;
        }

        String kid = signedJwt.getHeader().getKeyID();
        if (kid == null || kid.isBlank()) {
            throw new BadJwtException("EdDSA token is missing kid header");
        }

        try {
            JWKSet jwkSet = JWKSet.load(URI.create(jwkSetUri).toURL());
            JWK jwk = jwkSet.getKeys()
                    .stream()
                    .filter(key -> Objects.equals(key.getKeyID(), kid))
                    .findFirst()
                    .orElseThrow(() -> new BadJwtException("No JWK found for kid: " + kid));

            if (!(jwk instanceof OctetKeyPair octetKeyPair)) {
                throw new BadJwtException("JWK for kid " + kid + " is not an OKP key");
            }

            boolean signatureValid = signedJwt.verify(new Ed25519Verifier(octetKeyPair.toPublicJWK()));
            if (!signatureValid) {
                throw new BadJwtException("EdDSA signature verification failed for kid: " + kid);
            }

            Jwt jwt = toSpringJwt(token, signedJwt);
            OAuth2TokenValidatorResult validationResult = jwtValidator.validate(jwt);
            if (validationResult.hasErrors()) {
                OAuth2Error firstError = validationResult.getErrors().iterator().next();
                String description = firstError.getDescription() != null ? firstError.getDescription() : firstError.getErrorCode();
                throw new BadJwtException(description);
            }

            log.warn("JWT decoded through EdDSA fallback verifier: alg='{}', kid='{}'", algorithm, kid);
            return jwt;
        } catch (BadJwtException ex) {
            throw ex;
        } catch (JOSEException | ParseException ex) {
            throw new BadJwtException("EdDSA verification failed: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new BadJwtException("EdDSA key resolution failed: " + ex.getMessage(), ex);
        }
    }

    private Jwt toSpringJwt(String token, SignedJWT signedJwt) throws ParseException {
        JWTClaimsSet claimsSet = signedJwt.getJWTClaimsSet();
        Map<String, Object> headers = new LinkedHashMap<>(signedJwt.getHeader().toJSONObject());
        Map<String, Object> claims = new LinkedHashMap<>(claimsSet.getClaims());

        Jwt.Builder jwtBuilder = Jwt.withTokenValue(token)
                .headers(h -> h.putAll(headers))
                .claims(c -> c.putAll(claims));

        if (claimsSet.getIssueTime() != null) {
            jwtBuilder.issuedAt(claimsSet.getIssueTime().toInstant());
        }
        if (claimsSet.getNotBeforeTime() != null) {
            jwtBuilder.notBefore(claimsSet.getNotBeforeTime().toInstant());
        }
        if (claimsSet.getExpirationTime() != null) {
            jwtBuilder.expiresAt(claimsSet.getExpirationTime().toInstant());
        }
        return jwtBuilder.build();
    }

    private void logJwtDecodeFailure(String token, JwtException ex, String jwkSetUri) {
        Throwable rootCause = getRootCause(ex);
        int tokenLength = token != null ? token.length() : 0;
        int tokenSegments = token == null || token.isBlank() ? 0 : token.split("\\.", -1).length;
        if (token == null || token.isBlank()) {
            log.error(
                    "JWT decode failed: message='{}', exceptionType='{}', rootCauseType='{}', rootCauseMessage='{}', tokenMissing=true, tokenLength={}, tokenSegments={}",
                    ex.getMessage(),
                    ex.getClass().getName(),
                    rootCause.getClass().getName(),
                    rootCause.getMessage(),
                    tokenLength,
                    tokenSegments
            );
            return;
        }

        try {
            SignedJWT signedJwt = SignedJWT.parse(token);
            log.error(
                    "JWT decode failed: message='{}', exceptionType='{}', rootCauseType='{}', rootCauseMessage='{}', alg='{}', kid='{}', iss='{}', aud='{}', tokenLength={}, tokenSegments={}",
                    ex.getMessage(),
                    ex.getClass().getName(),
                    rootCause.getClass().getName(),
                    rootCause.getMessage(),
                    signedJwt.getHeader().getAlgorithm(),
                    signedJwt.getHeader().getKeyID(),
                    signedJwt.getJWTClaimsSet().getIssuer(),
                    signedJwt.getJWTClaimsSet().getAudience(),
                    tokenLength,
                    tokenSegments
            );
            logJwkLookupDiagnostics(jwkSetUri, signedJwt.getHeader().getKeyID(), signedJwt.getHeader().getAlgorithm().getName());
        } catch (ParseException parseException) {
            log.error(
                    "JWT decode failed: message='{}', exceptionType='{}', rootCauseType='{}', rootCauseMessage='{}', parseError='{}', tokenLength={}, tokenSegments={}",
                    ex.getMessage(),
                    ex.getClass().getName(),
                    rootCause.getClass().getName(),
                    rootCause.getMessage(),
                    parseException.getMessage(),
                    tokenLength,
                    tokenSegments
            );
        }
    }

    private void logJwkLookupDiagnostics(String jwkSetUri, String kid, String alg) {
        try {
            JWKSet jwkSet = JWKSet.load(URI.create(jwkSetUri).toURL());
            List<JWK> keys = jwkSet.getKeys();
            long kidMatches = keys.stream()
                    .filter(key -> Objects.equals(key.getKeyID(), kid))
                    .count();
            long algMatches = keys.stream()
                    .filter(key -> key.getAlgorithm() != null && Objects.equals(key.getAlgorithm().getName(), alg))
                    .count();
            long kidAndAlgMatches = keys.stream()
                    .filter(key -> Objects.equals(key.getKeyID(), kid))
                    .filter(key -> key.getAlgorithm() != null && Objects.equals(key.getAlgorithm().getName(), alg))
                    .count();
            log.error(
                    "JWT JWK diagnostics: jwkSetUri='{}', totalKeys={}, kid='{}', alg='{}', kidMatches={}, algMatches={}, kidAndAlgMatches={}",
                    jwkSetUri,
                    keys.size(),
                    kid,
                    alg,
                    kidMatches,
                    algMatches,
                    kidAndAlgMatches
            );
        } catch (Exception jwkException) {
            log.error(
                    "JWT JWK diagnostics failed: jwkSetUri='{}', kid='{}', alg='{}', exceptionType='{}', message='{}'",
                    jwkSetUri,
                    kid,
                    alg,
                    jwkException.getClass().getName(),
                    jwkException.getMessage()
            );
        }
    }

    private Throwable getRootCause(Throwable throwable) {
        Throwable root = throwable;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        return root;
    }
}
