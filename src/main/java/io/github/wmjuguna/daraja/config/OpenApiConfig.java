package io.github.wmjuguna.daraja.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi paymentsApi() {
        return GroupedOpenApi.builder()
                .group("payments")
                .pathsToMatch("/mobile/**")
                .displayName("M-Pesa Payment APIs")
                .build();
    }

    @Bean
    public GroupedOpenApi configurationApi() {
        return GroupedOpenApi.builder()
                .group("configuration")
                .pathsToMatch("/mobile/configure-paybill/**")
                .displayName("Configuration Management APIs")
                .build();
    }

    @Bean
    public OpenAPI darajaOpenAPI(@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri) {
        String openIdConfigurationUrl = issuerUri.endsWith("/")
                ? issuerUri + ".well-known/openid-configuration"
                : issuerUri + "/.well-known/openid-configuration";

        return new OpenAPI()
                .info(new Info()
                        .title("Daraja M-Pesa Gateway API")
                        .description("Comprehensive API documentation for Daraja M-Pesa Gateway Service. " +
                                "This service provides seamless integration with Safaricom's M-Pesa Daraja API, " +
                                "enabling STK Push payments, payment confirmations, validations, and merchant configuration management.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Daraja API Support")
                                .email("support@daraja.io")
                                .url("https://github.com/wmjuguna/daraja"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:30005")
                                .description("Local Server"),
                        new Server()
                                .url("https://cheddar.sarafrika.com")
                                .description("Production Server")))
                .tags(List.of(
                        new Tag()
                                .name("STK Push")
                                .description("STK Push payment initiation and callback handling"),
                        new Tag()
                                .name("Payment Processing")
                                .description("Payment confirmation and validation endpoints"),
                        new Tag()
                                .name("Configuration Management")
                                .description("Merchant paybill configuration management"),
                        new Tag()
                                .name("Payment Reports")
                                .description("Payment transaction reporting and analytics")))
                .components(new Components().addSecuritySchemes(
                        "oidc",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.OPENIDCONNECT)
                                .openIdConnectUrl(openIdConfigurationUrl)
                ))
                .addSecurityItem(new SecurityRequirement().addList("oidc"));
    }
}
