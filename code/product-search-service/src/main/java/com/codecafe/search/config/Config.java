package com.codecafe.search.config;

import com.codecafe.search.model.FacetData;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class Config {

    @Bean
    public JavaTimeModule javaTimeModule() {
        return new JavaTimeModule();
    }

    @Bean
    public Jdk8Module jdk8TimeModule() {
        return new Jdk8Module();
    }

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder mapperBuilder) {
        return mapperBuilder.build().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Bean
    public Map<String, FacetData> facetMap() {
        Map<String, FacetData> facetMap = new HashMap<>();
        facetMap.put("Categories", new FacetData().withDisplayName("Categories").withFieldName("categories"));
        facetMap.put("Brand", new FacetData().withDisplayName("Brand Name").withFieldName("brand"));
        facetMap.put("ColorFamily", new FacetData().withDisplayName("Color Family").withFieldName("generalAttributes.colorFamily"));
        return facetMap;
    }

}