package com.company.secureapispring.customer.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class OpenAPISecurityConfig {

    private final Environment env;
    private final BuildProperties buildProperties;

    private static final String OAUTH_SCHEME_NAME = "OAuthSecuritySchema";

    public OpenAPISecurityConfig(Environment env, BuildProperties buildProperties) {
        this.env = env;
        this.buildProperties = buildProperties;
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().components(new Components()
                        .addSecuritySchemes(OAUTH_SCHEME_NAME, createOAuthScheme()))
                .addSecurityItem(new SecurityRequirement().addList(OAUTH_SCHEME_NAME))
                .info(new Info().title(env.getRequiredProperty("app.name"))
                        .description(env.getRequiredProperty("app.description"))
                        .version(buildProperties.getVersion()))
                ;
    }

    private SecurityScheme createOAuthScheme() {
        OAuthFlows flows = createOAuthFlows();
        return new SecurityScheme().type(SecurityScheme.Type.OAUTH2)
                .flows(flows);
    }

    private OAuthFlows createOAuthFlows() {
        OAuthFlow flow = createAuthorizationCodeFlow();
        return new OAuthFlows().implicit(flow);
    }

    private OAuthFlow createAuthorizationCodeFlow() {
        String authorizationUrl = env.getRequiredProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri");
        return new OAuthFlow()
                .authorizationUrl(authorizationUrl + "/protocol/openid-connect/auth")
                .scopes(new Scopes().addString("read_access", "read data")
                        .addString("write_access", "modify data")
                )
                ;
    }
}
