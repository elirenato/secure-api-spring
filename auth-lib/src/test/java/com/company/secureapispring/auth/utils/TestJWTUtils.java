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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class TestJWTUtils {
    private final static String SECRET = "q3t6w9z$C&F)J@NcRfUjXnZr4u7x!A%D";
    private final static NimbusJwtEncoder JWT_ENCODER;
    private final static NimbusJwtDecoder JWT_DECODER;

    static {
        SecretKey key = new SecretKeySpec(SECRET.getBytes(), JWSAlgorithm.HS256.getName());

        JWKSource<SecurityContext> immutableSecret = new ImmutableSecret<>(key);
        JWT_ENCODER = new NimbusJwtEncoder(immutableSecret);

        JWT_DECODER = NimbusJwtDecoder.withSecretKey(key).build();
    }

    private TestJWTUtils() {}

    public static String encode(User user, Organization organization, String... roles) {
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
                    claims.put("organization", Collections.singletonList(organization.getAlias()));
                    claims.put("realm_access", realmAccess);
                })
                .build();
        JwsHeader jwsHeader = JwsHeader.with(JWSAlgorithm.HS256::getName).build();
        return JWT_ENCODER.encode(JwtEncoderParameters.from(jwsHeader, claimsSet)).getTokenValue();
    }

    public static Jwt decode(String jwtToken) {
        return JWT_DECODER.decode(jwtToken);
    }

    public static String getAuthHeader(User user, Organization organization, String... roles) {
        return "Bearer " + TestJWTUtils.encode(user, organization, roles);
    }
}
