package com.codecafe.search.helper;

import com.codecafe.search.config.FacetsConfiguration;
import com.codecafe.search.model.Filter;

import lombok.RequiredArgsConstructor;

import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.aggregations.Aggregation;
import org.opensearch.client.opensearch._types.aggregations.AggregationRange;
import org.opensearch.client.opensearch._types.query_dsl.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    Aggregation aggregation;

    if ("price".equals(facetCode)) {
      aggregation = new Aggregation.Builder().range(r -> r.field(facetCode)
                                                          .ranges(List.of(
                                                            AggregationRange.of(ar -> ar.from("0").to("100")),
                                                            AggregationRange.of(ar -> ar.from("100").to("200")),
                                                            AggregationRange.of(ar -> ar.from("200").to("300")),
                                                            AggregationRange.of(ar -> ar.from("300").to("400")),
                                                            AggregationRange.of(ar -> ar.from("400").to("500")),
                                                            AggregationRange.of(ar -> ar.from("500").to("600")),
                                                            AggregationRange.of(ar -> ar.from("600").to("700")),
                                                            AggregationRange.of(ar -> ar.from("700").to("800")),
                                                            AggregationRange.of(ar -> ar.from("800").to("900")),
                                                            AggregationRange.of(ar -> ar.from("900").to("1000")),
                                                            AggregationRange.of(ar -> ar.from("1000").to("2000")),
                                                            AggregationRange.of(ar -> ar.from("2000").to("3000")),
                                                            AggregationRange.of(ar -> ar.from("3000").to("4000"))
                                                          ))).build();
    } else {
      aggregation = new Aggregation.Builder().terms(t -> t.field(format(AGGREGATION_FIELD, facetCode))
                                                          .size(facetsSize)
                                                          .minDocCount(1)).build();
    }

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
    if (filter.getCode().equals("price")) {
      RangeQuery.Builder rangeQueryBuilder = QueryBuilders.range().field(filter.getCode());
      for (String value : filter.getValues()) {
        String[] range = value.split("-");
        rangeQueryBuilder = rangeQueryBuilder.from(JsonData.of(range[0])).to(JsonData.of(range[1]));
      }
      return rangeQueryBuilder.build()._toQuery();
    }
    return new TermsQuery.Builder().field(format(AGGREGATION_FIELD)).queryName(filter.getCode())
                                   .build()._toQuery();
  }

}
