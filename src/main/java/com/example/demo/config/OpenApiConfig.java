package com.example.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String API_TOKEN_SCHEME = "apiToken";

    @Bean
    public OpenAPI universityApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("University Management API")
                        .description("REST API for managing students, courses, and professors.")
                        .version("v1")
                        .contact(new Contact()
                                .name("University Platform Team")
                                .email("api-support@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .addSecurityItem(new SecurityRequirement().addList(API_TOKEN_SCHEME))
                .components(new Components().addSecuritySchemes(API_TOKEN_SCHEME, apiTokenScheme()));
    }

    private SecurityScheme apiTokenScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("X-API-TOKEN")
                .description("Static API token required in the X-API-TOKEN header");
    }
}
