package com.codecafe.search.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "app.search")
@PropertySource("classpath:facets.properties")
public class FacetsConfig {

  private Map<String, FacetInfo> facets;

  @Getter
  @Setter
  public static class FacetInfo {
    private String displayName;
  }

}
