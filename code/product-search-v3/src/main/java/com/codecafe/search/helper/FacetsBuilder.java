package com.codecafe.search.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.codecafe.search.config.FacetsConfiguration;

@Component
@RequiredArgsConstructor
public class FacetsBuilder {

  private static final String AGGREGATION_FIELD = "%s.raw";
  private final FacetsConfiguration facetsConfiguration;

  @Value("${app.search.facets-size:100}")
  private int facetsSize;

}
