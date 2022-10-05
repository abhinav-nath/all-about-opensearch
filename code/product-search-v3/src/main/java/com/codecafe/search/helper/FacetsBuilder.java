package com.codecafe.search.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opensearch.client.opensearch._types.aggregations.Aggregation;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.TermsQuery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.codecafe.search.config.FacetsConfiguration;
import com.codecafe.search.model.Filter;

import static java.lang.String.format;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
@RequiredArgsConstructor
public class FacetsBuilder {

  private static final String AGGREGATION_FIELD = "%s.raw";
  private final FacetsConfiguration facetsConfiguration;

  @Value("${app.search.facets-size:100}")
  private int facetsSize;

  Map<String, Aggregation> buildAggregations(List<Filter> filters) {

    Map<String, Aggregation> aggregations = new HashMap<>();

    for (String facetCode : facetsConfiguration.getFacets().keySet()) {
      Aggregation aggregation = buildAggregation(facetCode, filters);
      aggregations.put(facetCode, aggregation);
    }

    return aggregations;
  }

  Aggregation buildAggregation(String facetCode, List<Filter> filters) {
    Aggregation aggregation = new Aggregation.Builder().terms(t -> t.field(format(AGGREGATION_FIELD, facetCode))
                                                                    .size(facetsSize)
                                                                    .minDocCount(1)).build();
    if (!isEmpty(filters)) {
      BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

      filters.stream()
             .filter(filter -> !filter.getCode().equals(facetCode)) // filter out itself
             .forEach(filter -> boolQueryBuilder.filter(buildFilter(filter)));

      BoolQuery boolQuery = boolQueryBuilder.build();

      //build a filter aggregation
      if (!boolQuery.filter().isEmpty()) {
        aggregation = new Aggregation.Builder().filter(f -> f.bool(boolQuery)).build();
      }
    }

    return aggregation;
  }

  BoolQuery buildPostFilterIfApplicable(List<Filter> filters) {
    BoolQuery.Builder postFilterQueryBuilder = new BoolQuery.Builder();

    if (!isEmpty(filters)) {
      for (Filter filter : filters) {
        BoolQuery boolQuery = new BoolQuery.Builder().should(List.of(buildFilter(filter))).build();
        postFilterQueryBuilder.filter(List.of(boolQuery._toQuery()));
      }
    }

    BoolQuery postFilterQuery = postFilterQueryBuilder.build();
    postFilterQuery = postFilterQuery.filter().isEmpty() ? null : postFilterQuery;
    return postFilterQuery;
  }

  private Query buildFilter(Filter filter) {
    return new TermsQuery.Builder().field(format(AGGREGATION_FIELD)).queryName(filter.getCode())
                                   .build()._toQuery();
  }

}
