package com.codecafe.search.service;

import java.util.List;

import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.codecafe.search.document.ProductDocument;
import com.codecafe.search.helper.SearchResponseParser;
import com.codecafe.search.model.AutoSuggestResponse;
import com.codecafe.search.model.FacetData;
import com.codecafe.search.model.TextSearchResponse;
import com.codecafe.search.repository.SearchRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

  private final SearchRepository searchRepository;
  private final SearchResponseParser searchResponseParser;

  public AutoSuggestResponse getAutocompleteSuggestions(String query) {
    SearchResponse<ProductDocument> searchResponse = searchRepository.getAutocompleteSuggestions(query);
    log.info("Total search results returned: {}", searchResponse.hits().total().value());
    return searchResponseParser.toAutosuggestResponse(searchResponse);
  }

  public TextSearchResponse performTextSearch(String query, List<FacetData> facets, int page, int pageSize) {
    SearchResponse<ProductDocument> searchResponse = searchRepository.searchProducts(query, facets, page, pageSize);
    log.info("Total search results returned: {}", searchResponse.hits().total().value());
    return searchResponseParser.toTextSearchResponse(searchResponse);
  }

}
