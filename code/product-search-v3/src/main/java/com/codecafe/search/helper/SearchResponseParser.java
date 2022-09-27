package com.codecafe.search.helper;

import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.codecafe.search.config.FacetsConfiguration;
import com.codecafe.search.document.ProductDocument;
import com.codecafe.search.model.SearchResult;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class SearchResponseParser {

  private final ObjectMapper objectMapper;
  private final FacetsConfiguration facetsConfiguration;

  public SearchResult parseTextSearchResponse(SearchResponse<ProductDocument> searchResponse) {
    return new SearchResult();
  }

}
