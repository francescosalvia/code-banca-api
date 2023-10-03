package com.banca.project.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "sandbox")
public class SandboxConfig {

  private String basicUrl;
  private String authSchema;
  private String apiKey;
}
