package io.github.wmjuguna.daraja.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("com.sarafrika.malipo")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Payments Api Documentation")
                        .description("All exposed Api Endpoints for Mpesa Payments Integration With Daraja")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0").url("https://sarafrika.com")));
    }

}
