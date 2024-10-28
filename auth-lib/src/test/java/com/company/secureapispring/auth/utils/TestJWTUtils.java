package com.company.secureapispring.auth.utils;

import com.company.secureapispring.auth.entities.Organization;
import com.company.secureapispring.auth.entities.User;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.*;

public final class TestJWTUtils {
    private final static NimbusJwtEncoder JWT_ENCODER;

    static {
        String secret = Base64.getEncoder().encodeToString(new SecureRandom().generateSeed(32));
        SecretKey key = new SecretKeySpec(secret.getBytes(), JWSAlgorithm.HS256.getName());
        JWKSource<SecurityContext> immutableSecret = new ImmutableSecret<>(key);
        JWT_ENCODER = new NimbusJwtEncoder(immutableSecret);
    }

    private TestJWTUtils() {}

    public static Jwt createJwt(User user, List<String> organizationAliases, String... roles) {
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("http://localhost:8080/realms/app")
                .claims(claims -> {
                    Map<String, Object> realmAccess = new HashMap<>();
                    realmAccess.put("roles", roles);
                    claims.put("given_name", user.getGivenName());
                    claims.put("family_name", user.getFamilyName());
                    claims.put("preferred_username", user.getUsername());
                    claims.put("email", user.getEmail());
                    claims.put("email_verified", user.isEmailVerified());
                    if (organizationAliases != null) {
                        claims.put("organization", organizationAliases);
                    }
                    claims.put("realm_access", realmAccess);
                })
                .build();
        JwsHeader jwsHeader = JwsHeader.with(JWSAlgorithm.HS256::getName).build();
        return JWT_ENCODER.encode(JwtEncoderParameters.from(jwsHeader, claimsSet));
    }
}
