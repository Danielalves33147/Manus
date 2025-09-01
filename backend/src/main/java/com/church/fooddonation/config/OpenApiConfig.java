package com.church.fooddonation.config;

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
                        .title("Food Donation Management API")
                        .version("1.0.0")
                        .description("API para gerenciamento de doações de alimentos para instituições de caridade")
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento")
                                .email("contato@fooddonation.com")));
    }
}

