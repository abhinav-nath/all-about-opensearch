package com.codecafe.search.model;

import com.codecafe.search.model.SearchResponse.SearchResponseBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {

  private long totalResults;

  @Builder.Default
  private List<ProductHit> productHits = emptyList();

  @Builder.Default
  private List<Facet> facets = emptyList();

  public SearchResponse toSearchResponse(ModelMapper modelMapper) {
    SearchResponseBuilder searchResponseBuilder = SearchResponse.builder().totalResults(totalResults);
    if (totalResults > 0) {
      searchResponseBuilder.products(productHits.stream()
                                                .map(productHit -> modelMapper.map(productHit, ProductData.class))
                                                .collect(toList()))
                           .facets(facets);
    }

    return searchResponseBuilder.build();
  }

}