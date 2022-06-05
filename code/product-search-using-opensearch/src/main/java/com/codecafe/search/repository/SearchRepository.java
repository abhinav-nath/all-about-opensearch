package com.codecafe.search.repository;

import java.util.List;

import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.codecafe.search.helper.SearchRequestBuilder;
import com.codecafe.search.helper.SearchResponseParser;
import com.codecafe.search.model.FacetData;
import com.codecafe.search.model.PopularSearchResponse;
import com.codecafe.search.model.SearchResult;

import static org.opensearch.client.RequestOptions.DEFAULT;
import static org.opensearch.rest.RestStatus.CREATED;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SearchRepository {

  private final RestHighLevelClient restHighLevelClient;
  private final SearchRequestBuilder searchRequestBuilder;
  private final SearchResponseParser searchResponseParser;

  public SearchResult searchProducts(String query, List<FacetData> facets, String unitSystem, int page, int size) {
    SearchRequest searchRequest = searchRequestBuilder.buildTextSearchRequest(query, facets, unitSystem, page, size);

    log.info("Search JSON query: {}", searchRequest.source().toString());

    try {
      SearchResponse searchResponse = restHighLevelClient.search(searchRequest, DEFAULT);
      return searchResponseParser.parseTextSearchResponse(searchResponse, unitSystem);
    } catch (Exception ex) {
      log.error("Error in OpenSearch query", ex);
    }

    return null;
  }

  @Async
  public void saveSearchQuery(String query) {
    try {
      Thread.sleep(2000);
      IndexRequest indexRequest = searchRequestBuilder.buildSaveSearchQueryRequest(query);
      IndexResponse indexResponse = restHighLevelClient.index(indexRequest, DEFAULT);
      if (indexResponse != null && CREATED.equals(indexResponse.status())) {
        log.info("Search query saved successfully!");
      }
    } catch (Exception ex) {
      log.error("Error while storing the search query in opensearch", ex);
    }
  }

  public PopularSearchResponse getPopularSearchQueries(int top) {
    SearchRequest searchRequest = searchRequestBuilder.buildPopularSearchRequest(top);

    log.info("Popular Search JSON query: {}", searchRequest.source().toString());

    try {
      SearchResponse searchResponse = restHighLevelClient.search(searchRequest, DEFAULT);
      return searchResponseParser.parsePopularSearchResponse(searchResponse);
    } catch (Exception ex) {
      log.error("Error in OpenSearch query", ex);
    }

    return null;
  }

}