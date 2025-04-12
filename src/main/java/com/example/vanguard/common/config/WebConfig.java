package com.agridence.ctts.audit.common.config;

import com.agridence.ctts.audit.common.interceptor.RequestInterceptor;
import com.agridence.ctts.common.interceptors.JWTInterceptor;
import com.agridence.ctts.common.interceptors.builder.JWTBuilder;
import com.agridence.ctts.common.interceptors.builder.enums.JWTType;
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
@ComponentScan(basePackages = {"com.agridence.ctts.common.word.service"})
public class WebConfig implements WebMvcConfigurer {
  @Value("${jwt.issuer}")
  private String issuer;

  @Value("${jwt.audience}")
  private String audience;

  @Value("${jwt.jwk_uri}")
  private String jwkUri;

  @Value("${app.cors.registry.allowed.origins}")
  private String allowedOrigins;

  @Value("${app.cors.registry.allowed.methods}")
  private String allowsMethods;

  @Value("${app.cors.registry.allowed.origins.patterns}")
  private String allowedOriginPatterns;

  @Value("${app.cors.registry.path.pattern}")
  private String pathPattern;

  @Value("${iam.api.url}")
  private String iamBaseURL;

  @Value("${iam.service.authorization.enable}")
  private boolean authorizationEnabled;

  @Value("${spring.data.redis.host}")
  private String redisHost;

  @Value("${spring.data.redis.port}")
  private String redisPort;

  @Value("${jwt.user.info.url}")
  private String userInfoUrl;

  @Override
  public void addFormatters(FormatterRegistry registry) {
    ApplicationConversionService.configure(registry);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(new RequestInterceptor())
        .excludePathPatterns(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/v3/api-docs.json",
            "/actuator/**")
        .order(Ordered.LOWEST_PRECEDENCE);

    // JWTInterceptor is used to intercept the request and check for the JWT token
    registry
        .addInterceptor(buildJWTInterceptor())
        .excludePathPatterns(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "v3/api-docs.yaml",
            "/v3/api-docs.json",
            "actuator/**")
        .order(Ordered.HIGHEST_PRECEDENCE);
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

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // TODO: need to set up security for the application
    // WK: disabling csrf to support swagger POST requests
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
    return http.build();
  }

  private JWTInterceptor buildJWTInterceptor() {
    JWTBuilder userJWTBuilder =
        new JWTBuilder(issuer, audience, jwkUri)
            .setIamUrl(iamBaseURL)
            .setEmUrl(userInfoUrl)
            .setRedisHost(redisHost)
            .setRedisPort(redisPort)
            .setAuthorizationEnabled(authorizationEnabled)
            .setJwtType(JWTType.USER);
    return new JWTInterceptor(userJWTBuilder);
  }
}
