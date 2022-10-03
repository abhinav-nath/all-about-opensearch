package com.codecafe.search.repository;

import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonGenerator;

import java.io.StringWriter;
import java.util.List;

import org.opensearch.client.json.jackson.JacksonJsonpMapper;
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
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SearchRepository {

  private final ObjectMapper objectMapper;
  private final OpenSearchClient openSearchClient;
  private final SearchRequestBuilder searchRequestBuilder;
  private final SearchResponseParser searchResponseParser;

  private static void printQueryJson(SearchRequest searchRequest) {
    StringWriter writer = new StringWriter();
    JsonGenerator generator = JsonProvider.provider().createGenerator(writer);
    searchRequest.serialize(generator, new JacksonJsonpMapper());
    generator.flush();
    log.info("Search JSON query: {}", writer);
  }

  public SearchResponse<ProductDocument> getAutocompleteSuggestions(String query) {
    SearchRequest searchRequest = searchRequestBuilder.buildSuggestionsRequest(query);

    printQueryJson(searchRequest);

    try {
      return openSearchClient.search(searchRequest, ProductDocument.class);
    } catch (Exception ex) {
      log.error("Error in OpenSearch query", ex);
    }

    return null;
  }

  public SearchResponse<ProductDocument> searchProducts(String query, List<FacetData> facets, int page, int pageSize) {
    SearchRequest searchRequest = searchRequestBuilder.buildTextSearchRequest(query, facets, page, pageSize);

    printQueryJson(searchRequest);

    try {
      return openSearchClient.search(searchRequest, ProductDocument.class);
    } catch (Exception ex) {
      log.error("Error in OpenSearch query", ex);
    }

    return null;
  }

}
