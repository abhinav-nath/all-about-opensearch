package com.codecafe.search.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "app.search")
public class FacetsConfig {

    private Map<String, FacetInfo> facets;

    @Getter
    @Setter
    public static class FacetInfo {
        private String displayName;
    }

}