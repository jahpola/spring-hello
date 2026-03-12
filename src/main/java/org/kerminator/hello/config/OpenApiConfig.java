package org.kerminator.hello.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Hello Product API")
                        .version("0.0.1-SNAPSHOT")
                        .description("REST API for managing products")
                        .contact(new Contact()
                                .name("Kerminator")
                                .url("https://github.com/jahpola/spring-hello")));
    }
}
