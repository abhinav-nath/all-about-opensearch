package com.codecafe.search.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Facet {

  private String code;
  private String name;
  private List<FacetValue> facetValues;

}