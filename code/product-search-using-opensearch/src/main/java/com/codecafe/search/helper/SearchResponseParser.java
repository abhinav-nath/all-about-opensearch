package com.codecafe.search.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.opensearch.action.search.SearchResponse;
import org.opensearch.search.SearchHit;
import org.opensearch.search.aggregations.Aggregation;
import org.opensearch.search.aggregations.bucket.filter.ParsedFilter;
import org.opensearch.search.aggregations.bucket.range.ParsedRange;
import org.opensearch.search.aggregations.bucket.range.Range;
import org.opensearch.search.aggregations.bucket.terms.Terms;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.codecafe.search.config.FacetsConfiguration;
import com.codecafe.search.model.Facet;
import com.codecafe.search.model.FacetValue;
import com.codecafe.search.model.PopularSearchResponse;
import com.codecafe.search.model.ProductHit;
import com.codecafe.search.model.SearchQuery;
import com.codecafe.search.model.SearchResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
@RequiredArgsConstructor
public class SearchResponseParser {

  private final ObjectMapper objectMapper;
  private final FacetsConfiguration facetsConfiguration;

  public SearchResult parseTextSearchResponse(SearchResponse searchResponse, String unitSystem) {
    SearchResult.SearchResultBuilder searchResultBuilder = SearchResult.builder();

    long totalResults = searchResponse.getHits().getTotalHits().value;

    searchResultBuilder.totalResults(totalResults);

    if (totalResults > 0) {
      List<Map<String, Object>> sourceMaps = Arrays.stream(searchResponse.getHits().getHits())
                                                   .map(SearchHit::getSourceAsMap)
                                                   .collect(toList());

      List<ProductHit> matchedProducts = objectMapper.convertValue(sourceMaps, new TypeReference<>() {
      });

      searchResultBuilder.productHits(matchedProducts)
                         .facets(extractFacets(searchResponse, unitSystem));
    }

    return searchResultBuilder.build();
  }

  private List<Facet> extractFacets(SearchResponse searchResponse, String unitSystem) {
    List<Facet> facets = new ArrayList<>(1);

    if (searchResponse.getAggregations() == null) {
      return facets;
    }

    List<Aggregation> aggregationList = searchResponse.getAggregations().asList();

    for (Aggregation aggregation : aggregationList) {
      List<FacetValue> facetValues = new ArrayList<>(1);

      // check if sub-aggregation is present
      if (aggregation instanceof ParsedFilter) {
        for (Aggregation subAggregation : ((ParsedFilter) aggregation).getAggregations()) {
          parseFacets(subAggregation, facetValues);
        }
      } else {
        parseFacets(aggregation, facetValues);
      }

      if (!isEmpty(facetValues)) {

        Facet facet = Facet.builder()
                           .code(aggregation.getName())
                           .name(facetsConfiguration.getFacets().get(aggregation.getName()).getDisplayName())
                           .facetValues(facetValues)
                           .build();
        if (facetsConfiguration.getFacets().get(aggregation.getName()).isMeasurement()) {
          facet.setUnit(facetsConfiguration.getFacets().get(aggregation.getName()).getMeasurementUnits().getOrDefault(unitSystem, "default"));
        }
        facets.add(facet);
      }
    }

    facets.sort(Comparator.comparing(facet -> facetsConfiguration.getFacets().get(facet.getCode()).getSequence()));

    return facets;
  }

  private void parseFacets(Aggregation aggregation, List<FacetValue> facetValues) {
    if (aggregation instanceof ParsedRange) {
      for (Range.Bucket bucket : ((Range) aggregation).getBuckets()) {
        if (bucket.getDocCount() > 0) {
          facetValues.add(FacetValue.builder().name(bucket.getKeyAsString()).count(bucket.getDocCount()).build());
        }
      }
    } else {
      for (Terms.Bucket bucket : ((Terms) aggregation).getBuckets()) {
        facetValues.add(FacetValue.builder().name(bucket.getKeyAsString()).count(bucket.getDocCount()).build());
      }
    }
  }

  public PopularSearchResponse parsePopularSearchResponse(SearchResponse searchResponse) {
    List<SearchQuery> searchQueries = new ArrayList<>(1);

    if (searchResponse.getAggregations() == null ||
      (searchResponse.getAggregations() != null && isEmpty(searchResponse.getAggregations().asList()))) {
      return null;
    }

    List<Aggregation> aggregations = searchResponse.getAggregations().asList();

    for (Terms.Bucket bucket : ((Terms) aggregations.get(0)).getBuckets()) {
      searchQueries.add(SearchQuery.builder()
                                   .query(bucket.getKeyAsString())
                                   .count(bucket.getDocCount())
                                   .build());
    }

    return PopularSearchResponse.builder().searchQueries(searchQueries).build();
  }

}