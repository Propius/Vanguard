package com.example.vanguard.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Value("${app.cors.registry.allowed.origins}")
  private String allowedOrigins;

  @Value("${app.cors.registry.allowed.methods}")
  private String allowsMethods;

  @Value("${app.cors.registry.allowed.origins.patterns}")
  private String allowedOriginPatterns;

  @Value("${app.cors.registry.path.pattern}")
  private String pathPattern;

  @Override
  public void addFormatters(FormatterRegistry registry) {
    ApplicationConversionService.configure(registry);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    String[] allowedMethodsVarArgs = allowsMethods.split(",");
    registry
        .addMapping(pathPattern)
        .allowedMethods(allowedMethodsVarArgs)
        .allowedOrigins(allowedOrigins)
        .allowedOriginPatterns(allowedOriginPatterns);
  }
  
}
