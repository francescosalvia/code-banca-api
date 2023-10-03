package com.banca.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

  @Value("${swagger.enabled}")
  private boolean swaggerEnabled;

  @Value("${swagger.protocols}")
  private String[] swaggerProtocols;

  @Value("${swagger.hostname}")
  private String swaggerHostname;

  @Bean
  public Docket api() {

    Set<String> protocols = new HashSet<>(Arrays.asList(swaggerProtocols));

    return new Docket(DocumentationType.SWAGGER_2)
        .enable(swaggerEnabled)
        .groupName("Version 1.0")
        .select()
        .paths(regex("/v1/*.*"))
        .build()
        .apiInfo(
            new ApiInfoBuilder()
                .version("1.0")
                .title("Bank api project")
                .description("API Documentation v1.0")
                .build())
        .protocols(protocols)
        .securitySchemes(Collections.singletonList(apiKey()))
        .useDefaultResponseMessages(false)
        .host(swaggerHostname);
  }

  private ApiKey apiKey() {
    return new ApiKey("apiKey", "Authorization", "header");
  }
}
