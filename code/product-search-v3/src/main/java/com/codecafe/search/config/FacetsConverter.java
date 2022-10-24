package com.codecafe.search.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.codecafe.search.model.Filter;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class FacetsConverter implements Converter<String, List<Filter>> {

  private final ObjectMapper objectMapper;

  @Override
  public List<Filter> convert(String source) {
    try {
      return Arrays.asList(objectMapper.readValue(source, Filter[].class));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}