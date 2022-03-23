package com.codecafe.search.config;

import com.codecafe.search.model.FacetData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class FacetsConverter implements Converter<String, List<FacetData>> {

  private ObjectMapper objectMapper;

  public FacetsConverter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public List<FacetData> convert(String source) {
    try {
      return Arrays.asList(objectMapper.readValue(source, FacetData[].class));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}