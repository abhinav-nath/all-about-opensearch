package com.codecafe.search.model;

import java.util.List;

import org.modelmapper.ModelMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.codecafe.search.model.TextSearchResponse.TextSearchResponseBuilder;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {

  private long totalResults;

  @Builder.Default
  private List<String> didYouMean = emptyList();

  @Builder.Default
  private List<ProductHit> productHits = emptyList();

  @Builder.Default
  private List<Facet> facets = emptyList();

  public TextSearchResponse toSearchResponse(ModelMapper modelMapper) {
    TextSearchResponseBuilder textSearchResponseBuilder = TextSearchResponse.builder()
                                                                            .totalResults(totalResults)
                                                                            .didYouMean(didYouMean);
    if (totalResults > 0) {
      textSearchResponseBuilder.products(productHits.stream()
                                                    .map(productHit -> modelMapper.map(productHit, ProductData.class))
                                                    .collect(toList()))
                               .facets(facets);
    }

    return textSearchResponseBuilder.build();
  }

}