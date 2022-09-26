package com.codecafe.search.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.codecafe.search.config.FacetsConfig;
import com.codecafe.search.document.ProductDocument;

import static org.springframework.util.CollectionUtils.isEmpty;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {

  private long totalResults;
  private List<ProductDocument> productDocuments;
  private Aggregations aggregations;

  public SearchResponse toDto(Map<String, FacetsConfig.FacetInfo> facetsConfig) {
    List<Product> products = new ArrayList<>(1);
    List<Facet> facets = null;

    if (!isEmpty(productDocuments)) {
      productDocuments.forEach(p -> products.add(p.toDto()));
    }

    if (aggregations != null) {
      facets = getFacets(facetsConfig);
    }

    return SearchResponse.builder()
      .products(products)
      .totalResults(totalResults)
      .facets(facets)
      .build();
  }

  private List<Facet> getFacets(Map<String, FacetsConfig.FacetInfo> facetsConfig) {
    List<Facet> facets = new ArrayList<>(1);
    List<Aggregation> aggregations = this.aggregations.asList();

    for (Aggregation aggregation : aggregations) {
      List<FacetValue> facetValues = new ArrayList<>(1);

      for (Terms.Bucket bucket : ((Terms) aggregation).getBuckets()) {
        FacetValue facetValue = FacetValue.builder()
          .name(bucket.getKeyAsString())
          .count(bucket.getDocCount())
          .build();
        facetValues.add(facetValue);
      }

      if (!isEmpty(facetValues)) {
        Facet facet = Facet.builder()
          .code(aggregation.getName())
          .name(facetsConfig.get(aggregation.getName()).getDisplayName())
          .facetValues(facetValues)
          .build();

        facets.add(facet);
      }
    }

    return facets;
  }

}
