package com.codecafe.search.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextSearchResponse {

  private long totalResults;
  private List<String> didYouMean;
  private List<ProductData> products;
  private List<Facet> facets;

}