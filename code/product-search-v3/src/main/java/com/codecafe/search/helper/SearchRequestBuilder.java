package com.codecafe.search.helper;

import java.util.List;

import org.opensearch.client.opensearch.core.SearchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.codecafe.search.config.OpenSearchConfiguration;
import com.codecafe.search.model.FacetData;

@Component
@RequiredArgsConstructor
public class SearchRequestBuilder {

  private final FacetsBuilder facetsBuilder;
  private final OpenSearchConfiguration openSearchConfiguration;

  @Value("${app.search.popular.from.days:-1}")
  private int popularInLastNDays;

  public SearchRequest buildTextSearchRequest(String query, List<FacetData> facets, int page, int size) {

    return new SearchRequest.Builder().build();
  }

}
