package com.banca.project;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = {"com.banca.*"})
@ConfigurationPropertiesScan("com.banca.project")
@Slf4j
public class Application {

  public static void main(String[] args) {

    try {
      SpringApplication app = new SpringApplication(Application.class);
      app.addListeners(new ApplicationPidFileWriter());
      app.run(args);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
