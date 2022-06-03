package com.codecafe.search.helper;

import java.util.List;

import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.RangeQueryBuilder;
import org.opensearch.search.aggregations.AggregationBuilder;
import org.opensearch.search.aggregations.AggregationBuilders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.codecafe.search.config.FacetsConfiguration;
import com.codecafe.search.model.FacetData;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.opensearch.index.query.QueryBuilders.termsQuery;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
public class FacetsBuilder {

  private static final String AGGREGATION_FIELD = "%s.raw";

  @Value("${app.search.facets-size:100}")
  private int facetsSize;

  private final FacetsConfiguration facetsConfiguration;

  public FacetsBuilder(FacetsConfiguration facetsConfiguration) {
    this.facetsConfiguration = facetsConfiguration;
  }

  List<AggregationBuilder> buildAggregations(List<FacetData> facets, String unitSystem) {
    return facetsConfiguration.getFacets()
                              .keySet()
                              .stream()
                              .map(facetCode -> buildAggregation(facetCode, facets, unitSystem))
                              .collect(toList());
  }

  AggregationBuilder buildAggregation(String facet, List<FacetData> filters, String unitSystem) {
    AggregationBuilder aggregationBuilder;

    if ("price".equals(facet)) {
      aggregationBuilder =
        AggregationBuilders.range(facet).field(facet).addRange(0, 100).addRange(100, 200).addRange(200, 300).addRange(300,
          400).addRange(400, 500).addRange(500, 600).addRange(600, 700).addRange(700, 800).addRange(800, 900).addRange(900,
          1000).addRange(1000, 2000).addRange(2000, 3000).addRange(3000, 4000);
    } else {
      if (facetsConfiguration.getFacets().get(facet).isMeasurement()) {
        String unit = facetsConfiguration.getFacets().get(facet).getMeasurementUnits().getOrDefault(unitSystem, "default");
        aggregationBuilder =
          AggregationBuilders.terms(facet).field(facet + "_" + unit).size(facetsSize).minDocCount(1);
      } else {
        aggregationBuilder =
          AggregationBuilders.terms(facet).field(format(AGGREGATION_FIELD, facet)).size(facetsSize).minDocCount(1);
      }
    }

    if (!isEmpty(filters)) {
      BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
      filters.stream()
             .filter(filter -> !filter.getCode().equals(facet)) // filter out itself
             .forEach(filter -> queryBuilder.filter(buildFilter(filter)));

      //build a filter aggregation
      if (!queryBuilder.filter().isEmpty()) {
        aggregationBuilder = AggregationBuilders.filter(facet, queryBuilder).subAggregation(aggregationBuilder);
      }
    }

    return aggregationBuilder;
  }

  BoolQueryBuilder buildPostFilterIfApplicable(List<FacetData> facets) {
    BoolQueryBuilder postFilterQuery = QueryBuilders.boolQuery();

    if (!isEmpty(facets)) {
      for (FacetData filter : facets) {
        BoolQueryBuilder orQueryBuilder = QueryBuilders.boolQuery();

        orQueryBuilder.should(buildFilter(filter));

        postFilterQuery.filter(orQueryBuilder);
      }
    }

    postFilterQuery = postFilterQuery.filter().isEmpty() ? null : postFilterQuery;
    return postFilterQuery;
  }

  private QueryBuilder buildFilter(FacetData filter) {
    if (filter.getCode().equals("price")) {
      RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(filter.getCode());
      for (String value : filter.getValues()) {
        String[] range = value.split("-");
        rangeQueryBuilder = rangeQueryBuilder.from(range[0]).to(range[1]);
      }
      return rangeQueryBuilder;
    }
    return termsQuery(format(AGGREGATION_FIELD, filter.getCode()), filter.getValues());
  }

}
