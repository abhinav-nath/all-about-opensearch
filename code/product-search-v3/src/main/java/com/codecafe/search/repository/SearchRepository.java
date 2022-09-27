package com.codecafe.search.repository;

import java.util.List;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.codecafe.search.document.ProductDocument;
import com.codecafe.search.helper.SearchRequestBuilder;
import com.codecafe.search.helper.SearchResponseParser;
import com.codecafe.search.model.FacetData;
import com.codecafe.search.model.SearchResult;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SearchRepository {

  private final OpenSearchClient openSearchClient;
  private final SearchRequestBuilder searchRequestBuilder;
  private final SearchResponseParser searchResponseParser;

  public SearchResult searchProducts(String query, List<FacetData> facets, int page, int size) {
    SearchRequest searchRequest = searchRequestBuilder.buildTextSearchRequest(query, facets, page, size);

    log.info("Search JSON query: {}", searchRequest.source().toString());

    try {
      SearchResponse<ProductDocument> searchResponse = openSearchClient.search(searchRequest, ProductDocument.class);
      return searchResponseParser.parseTextSearchResponse(searchResponse);
    } catch (Exception ex) {
      log.error("Error in OpenSearch query", ex);
    }

    return null;
  }

}
