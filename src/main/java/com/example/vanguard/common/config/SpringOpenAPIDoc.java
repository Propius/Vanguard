package com.example.vanguard.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class SpringOpenAPIDoc {

  @Bean
  OpenAPI customOpenAPI() {
    final String apiTitle = String.format("%s API", StringUtils.capitalize("Vanguard"));
    return new OpenAPI()
        .info(new Info().title(apiTitle).version("1.0"));
  }
}
