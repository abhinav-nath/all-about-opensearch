package com.codecafe.search.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opensearch.client.opensearch._types.aggregations.Aggregate;
import org.opensearch.client.opensearch._types.aggregations.StringTermsBucket;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.codecafe.search.config.FacetsConfiguration;
import com.codecafe.search.document.ProductDocument;
import com.codecafe.search.model.AutoSuggestResponse;
import com.codecafe.search.model.Facet;
import com.codecafe.search.model.FacetValue;
import com.codecafe.search.model.ProductData;
import com.codecafe.search.model.TextSearchResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchResponseParser {

  private final ObjectMapper objectMapper;
  private final FacetsConfiguration facetsConfiguration;

  public AutoSuggestResponse toAutosuggestResponse(SearchResponse<ProductDocument> searchResponse) {
    List<String> autosuggestions = new ArrayList<>();
    List<Hit<ProductDocument>> hits = searchResponse.hits().hits();

    for (Hit<ProductDocument> hit : hits) {
      ProductDocument product = hit.source();
      autosuggestions.add(product.getName());
    }

    return AutoSuggestResponse.builder().suggestions(autosuggestions).build();
  }

  public TextSearchResponse toTextSearchResponse(SearchResponse<ProductDocument> searchResponse) {

    List<ProductData> productDataList = new ArrayList<>();
    long totalResults = searchResponse.hits().total().value();

    if (totalResults > 0) {
      List<Hit<ProductDocument>> hits = searchResponse.hits().hits();

      for (Hit<ProductDocument> hit : hits) {
        ProductDocument product = hit.source();
        log.info("Found product " + product.getName() + ", score " + hit.score());

        ProductData productData = ProductData.builder()
                                             .code(product.getCode())
                                             .name(product.getName())
                                             .description(product.getDescription())
                                             .brand(product.getBrand())
                                             .color(product.getColor())
                                             .department(product.getDepartment())
                                             .build();
        productDataList.add(productData);
      }
    }

    return TextSearchResponse.builder()
                             .totalResults(totalResults)
                             .products(productDataList)
                             .facets(searchResponse.aggregations() != null ? extractFacets(searchResponse.aggregations()) : null)
                             .build();
  }

  private List<Facet> extractFacets(Map<String, Aggregate> aggregations) {
    List<Facet> facets = new ArrayList<>(1);

    if (aggregations == null) {
      return facets;
    }

    for (Map.Entry<String, Aggregate> entry : aggregations.entrySet()) {
      Aggregate aggregate = entry.getValue();
      List<FacetValue> facetValues = null;

      if (aggregate.isFilter()) {
        for(Map.Entry<String, Aggregate> agg : aggregate.filter().aggregations().entrySet()) {
          facetValues = extractFacetValues(agg.getValue()._get()._toAggregate());
        }
      } else {
        facetValues = extractFacetValues(aggregate);
      }

      if (!isEmpty(facetValues)) {
        Facet facet = Facet.builder()
                           .code(entry.getKey())
                           .name(facetsConfiguration.getFacets().get(entry.getKey()).getDisplayName())
                           .facetValues(facetValues)
                           .build();
        facets.add(facet);
      }
    }

    return facets;
  }

  private List<FacetValue> extractFacetValues(Aggregate aggregate) {
    List<FacetValue> facetValues = new ArrayList<>(1);

    for (StringTermsBucket bucket : aggregate.sterms().buckets().array()) {
      facetValues.add(FacetValue.builder().name(bucket.key()).count(bucket.docCount()).build());
    }

    return facetValues;
  }

}
