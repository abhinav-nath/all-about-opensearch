package com.codecafe.search.helper;

import com.codecafe.search.config.FacetsConfig;
import com.codecafe.search.model.FacetData;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.aggregations.AggregationBuilder;
import org.opensearch.search.aggregations.AggregationBuilders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
public class FacetsBuilder {

  private static final String AGGREGATION_FIELD = "%s.raw";

  @Value("${app.search.facets-size:100}")
  private int facetsSize;

  private final FacetsConfig facetsConfig;

  public FacetsBuilder(FacetsConfig facetsConfig) {
    this.facetsConfig = facetsConfig;
  }

  List<AggregationBuilder> buildAggregations(List<FacetData> facets) {
    return facetsConfig.getFacets()
                       .keySet()
                       .stream()
                       .map(facetCode -> buildAggregation(facetCode, facets))
                       .collect(toList());
  }

  AggregationBuilder buildAggregation(String facet, List<FacetData> filters) {
    AggregationBuilder aggregationBuilder = AggregationBuilders.terms(facet).field(String.format(AGGREGATION_FIELD, facet)).size(facetsSize);

    BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
    filters.stream()
           .filter(filter -> !filter.getCode().equals(facet)) // filter out itself
           .forEach(filter -> queryBuilder.filter(QueryBuilders.termsQuery(String.format(AGGREGATION_FIELD, filter.getCode()), filter.getValues())));

    //build a filter aggregation
    if (!queryBuilder.filter().isEmpty()) {
      aggregationBuilder = AggregationBuilders.filter(facet, queryBuilder).subAggregation(aggregationBuilder);
    }
    return aggregationBuilder;
  }

  BoolQueryBuilder buildPostFilterIfApplicable(List<FacetData> facets) {
    BoolQueryBuilder postFilterQuery = QueryBuilders.boolQuery();

    if (!isEmpty(facets)) {
      for (FacetData filter : facets) {
        BoolQueryBuilder orQueryBuilder = QueryBuilders.boolQuery();

        orQueryBuilder.should(QueryBuilders.termsQuery(String.format(AGGREGATION_FIELD, filter.getCode()), filter.getValues()));

        postFilterQuery.filter(orQueryBuilder);
      }
    }

    postFilterQuery = postFilterQuery.filter().isEmpty() ? null : postFilterQuery;
    return postFilterQuery;
  }

}
