package app.fortuneconnect.payments.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
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
                .group("bankwave-cbs")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Payments Api Documentation")
                        .description("All exposed Api Endpoints for Payments Gateway")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0").url("https://fortuneconnectltd.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("Fortune Connect Payments Api Docs")
                        .url("https://springshop.wiki.github.org/docs"));
    }

}
