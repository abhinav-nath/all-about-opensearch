package com.codecafe.search.helper;

import com.codecafe.search.config.FacetsConfig;
import com.codecafe.search.model.Facet;
import com.codecafe.search.model.FacetValue;
import com.codecafe.search.model.ProductHit;
import com.codecafe.search.model.SearchResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.search.SearchHit;
import org.opensearch.search.aggregations.Aggregation;
import org.opensearch.search.aggregations.bucket.filter.ParsedFilter;
import org.opensearch.search.aggregations.bucket.terms.Terms;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
public class SearchResponseParser {

  private final ObjectMapper objectMapper;
  private final FacetsConfig facetsConfig;

  public SearchResponseParser(ObjectMapper objectMapper, FacetsConfig facetsConfig) {
    this.objectMapper = objectMapper;
    this.facetsConfig = facetsConfig;
  }

  public SearchResult parseSearchResult(SearchResponse searchResponse) {
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
                         .facets(extractFacets(searchResponse));
    }

    return searchResultBuilder.build();
  }

  private List<Facet> extractFacets(SearchResponse searchResponse) {
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
          for (Terms.Bucket bucket : ((Terms) subAggregation).getBuckets()) {
            facetValues.add(FacetValue.builder().name(bucket.getKeyAsString()).count(bucket.getDocCount()).build());
          }
        }
      } else {
        for (Terms.Bucket bucket : ((Terms) aggregation).getBuckets()) {
          facetValues.add(FacetValue.builder().name(bucket.getKeyAsString()).count(bucket.getDocCount()).build());
        }
      }

      if (!isEmpty(facetValues)) {
        facets.add(Facet.builder()
                        .code(aggregation.getName())
                        .name(facetsConfig.getFacets().get(aggregation.getName()).getDisplayName())
                        .facetValues(facetValues).build());
      }
    }

    return facets;
  }

}