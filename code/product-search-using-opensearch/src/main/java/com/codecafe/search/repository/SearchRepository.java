package com.codecafe.search.repository;

import com.codecafe.search.helper.SearchRequestBuilder;
import com.codecafe.search.helper.SearchResponseParser;
import com.codecafe.search.model.FacetData;
import com.codecafe.search.model.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.opensearch.client.RequestOptions.DEFAULT;

@Slf4j
@Repository
public class SearchRepository {

  private final RestHighLevelClient restHighLevelClient;
  private final SearchRequestBuilder searchRequestBuilder;
  private final SearchResponseParser searchResponseParser;

  @Autowired
  public SearchRepository(RestHighLevelClient restHighLevelClient, SearchRequestBuilder searchRequestBuilder, SearchResponseParser searchResponseParser) {
    this.restHighLevelClient = restHighLevelClient;
    this.searchRequestBuilder = searchRequestBuilder;
    this.searchResponseParser = searchResponseParser;
  }

  public SearchResult searchProducts(String query, List<FacetData> facets, int page, int size) {
    SearchRequest searchRequest = searchRequestBuilder.buildTextSearchRequest(query, facets, page, size);

    log.info("Search JSON query: {}", searchRequest.source().toString());

    try {
      SearchResponse searchResponse = restHighLevelClient.search(searchRequest, DEFAULT);
      return searchResponseParser.parseSearchResult(searchResponse);
    } catch (Exception ex) {
      log.error("Error in OpenSearch query", ex);
    }

    return null;
  }

}