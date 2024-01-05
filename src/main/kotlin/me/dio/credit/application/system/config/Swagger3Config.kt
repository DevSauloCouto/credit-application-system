package me.dio.credit.application.system.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.License
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition
class Swagger3Config {

    @Bean
    fun openApiConfig(): OpenAPI? {
        return OpenAPI().info(
            Info()
                .title("System Credit Application")
                .description("Sistema de Avaliação de Solicitações de Empréstimos")
                .version("v0.0.1")
                .license(
                License()
                    .name("Apache 2.0")
                    .url("https://github.com/DevSauloCouto/credit-application-system")
                )

        )
    }

    @Bean
    fun publicApi(): GroupedOpenApi? {
        return GroupedOpenApi.builder()
            .group("springcreditapplicationsystem-public")
            .pathsToMatch("/api/customers/**", "/api/credits/**")
            .build();
    }

}