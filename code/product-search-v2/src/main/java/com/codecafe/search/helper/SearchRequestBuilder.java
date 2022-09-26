package com.codecafe.search.helper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.FuzzyQueryBuilder;
import org.opensearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.opensearch.index.query.MatchQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.RangeQueryBuilder;
import org.opensearch.index.query.WildcardQueryBuilder;
import org.opensearch.search.aggregations.AggregationBuilder;
import org.opensearch.search.aggregations.AggregationBuilders;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.sort.FieldSortBuilder;
import org.opensearch.search.sort.SortBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.codecafe.search.config.OpenSearchConfiguration;
import com.codecafe.search.model.FacetData;

import static org.opensearch.index.query.QueryBuilders.boolQuery;
import static org.opensearch.search.sort.SortBuilders.fieldSort;
import static org.opensearch.search.sort.SortBuilders.scoreSort;
import static org.opensearch.search.sort.SortOrder.DESC;

@Component
@RequiredArgsConstructor
public class SearchRequestBuilder {

  @Value("${app.search.popular.from.days:-1}")
  private int popularInLastNDays;

  private final FacetsBuilder facetsBuilder;
  private final OpenSearchConfiguration openSearchConfiguration;

  public SearchRequest buildTextSearchRequest(String query, List<FacetData> facets, String unitSystem, int page, int size) {
    QueryBuilder queryBuilder = buildBasicTextSearchQuery(query);

    SearchRequest searchRequest = buildSearchRequestFrom(queryBuilder, facets, unitSystem, page, size);
    addSorting(searchRequest);
    return searchRequest;
  }

  private BoolQueryBuilder buildBasicTextSearchQuery(String query) {
    return boolQuery()
      .should(new MatchQueryBuilder("name", query).boost(10.0f))
      .should(new MatchQueryBuilder("description", query).boost(1.0f))
      .should(new FuzzyQueryBuilder("name", query).boost(10.0f))
      .should(new WildcardQueryBuilder("name", "*" + query + "*").boost(10.0f))
      .should(new MatchPhrasePrefixQueryBuilder("name", query).boost(10.0f));
  }

  private SearchRequest buildSearchRequestFrom(QueryBuilder queryBuilder, List<FacetData> facets, String unitSystem, int page, int size) {
    SearchRequest searchRequest = new SearchRequest(openSearchConfiguration.getOpenSearchProperties().getIndices().get(0).getName());
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.version(true);

    sourceBuilder.query(queryBuilder);

    sourceBuilder.from((page - 1) * size);
    sourceBuilder.size(size);

    facetsBuilder.buildAggregations(facets, unitSystem).forEach(sourceBuilder::aggregation);

    BoolQueryBuilder postFilterQuery = facetsBuilder.buildPostFilterIfApplicable(facets, unitSystem);

    searchRequest.source(sourceBuilder.postFilter(postFilterQuery));
    return searchRequest;
  }

  private void addSorting(SearchRequest searchRequest) {
    FieldSortBuilder modifiedAtSortBuilder = fieldSort("modifiedAt").order(DESC);
    FieldSortBuilder createdAtSortBuilder = fieldSort("createdAt").order(DESC);
    List<SortBuilder<? extends SortBuilder<?>>> sortOrders = List.of(scoreSort(), modifiedAtSortBuilder, createdAtSortBuilder);
    sortOrders.forEach(searchRequest.source()::sort);
  }

  public IndexRequest buildSaveSearchQueryRequest(String query) {
    IndexRequest indexRequest =
      new IndexRequest(openSearchConfiguration.getOpenSearchProperties().getIndices().get(1).getName());

    Map<String, Object> jsonMap = new HashMap<>();
    jsonMap.put("query", query);
    jsonMap.put("timestamp", new Date());

    indexRequest.source(jsonMap);
    return indexRequest;
  }

  public SearchRequest buildPopularSearchRequest(int top) {
    SearchRequest searchRequest = new SearchRequest(openSearchConfiguration.getOpenSearchProperties().getIndices().get(1).getName());
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.version(true);
    sourceBuilder.from(0);
    sourceBuilder.size(0);

    if (popularInLastNDays > -1) {
      String fromDate = String.format("now-%dd/d", popularInLastNDays);
      RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder("timestamp").gte(fromDate);
      sourceBuilder.query(rangeQueryBuilder);
    }

    AggregationBuilder aggregationBuilder = AggregationBuilders.terms("searchQueries").field("query").size(top);

    sourceBuilder.aggregation(aggregationBuilder);
    return searchRequest.source(sourceBuilder);
  }

}