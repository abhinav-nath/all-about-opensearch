package com.codecafe.search.helper;

import com.codecafe.search.model.FacetData;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.FuzzyQueryBuilder;
import org.opensearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.opensearch.index.query.MatchQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.WildcardQueryBuilder;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.sort.FieldSortBuilder;
import org.opensearch.search.sort.SortBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.opensearch.index.query.QueryBuilders.boolQuery;
import static org.opensearch.search.sort.SortBuilders.fieldSort;
import static org.opensearch.search.sort.SortBuilders.scoreSort;
import static org.opensearch.search.sort.SortOrder.DESC;

@Component
public class SearchRequestBuilder {

  @Value("${app.opensearch.index-name}")
  private String indexName;

  private final FacetsBuilder facetsBuilder;

  public SearchRequestBuilder(FacetsBuilder facetsBuilder) {
    this.facetsBuilder = facetsBuilder;
  }

  public SearchRequest buildTextSearchRequest(String query, List<FacetData> facets, int page, int size) {
    QueryBuilder queryBuilder = buildBasicTextSearchQuery(query);

    SearchRequest searchRequest = buildSearchRequestFrom(queryBuilder, facets, page, size);
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

  private SearchRequest buildSearchRequestFrom(QueryBuilder queryBuilder, List<FacetData> facets, int page, int size) {
    SearchRequest searchRequest = new SearchRequest(indexName);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.version(true);

    sourceBuilder.query(queryBuilder);

    sourceBuilder.from((page - 1) * size);
    sourceBuilder.size(size);

    facetsBuilder.buildAggregations(facets).forEach(sourceBuilder::aggregation);

    BoolQueryBuilder postFilterQuery = facetsBuilder.buildPostFilterIfApplicable(facets);

    searchRequest.source(sourceBuilder.postFilter(postFilterQuery));
    return searchRequest;
  }

  private void addSorting(SearchRequest searchRequest) {
    FieldSortBuilder modifiedAtSortBuilder = fieldSort("modifiedAt").order(DESC);
    FieldSortBuilder createdAtSortBuilder = fieldSort("createdAt").order(DESC);
    List<SortBuilder<? extends SortBuilder<?>>> sortOrders = List.of(scoreSort(), modifiedAtSortBuilder, createdAtSortBuilder);
    sortOrders.forEach(searchRequest.source()::sort);
  }

}
