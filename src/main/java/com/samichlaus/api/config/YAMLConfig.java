package com.samichlaus.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration("config")
@ConfigurationProperties(prefix = "samichlaus")
@Data
public class YAMLConfig {

  private String backendServer;
  private String frontendServer;
  private String pathToOsmFile;
  private String pathToGraphhopperData;
  private String pathToExcelTemplate;
  private String pathToSamichlausIcon;
}
