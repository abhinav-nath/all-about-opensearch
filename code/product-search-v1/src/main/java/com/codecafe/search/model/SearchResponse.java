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
public class SearchResponse {

  private long totalResults;
  private List<Product> products;
  private List<Facet> facets;

}
