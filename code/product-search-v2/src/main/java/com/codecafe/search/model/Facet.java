package com.codecafe.search.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Facet {

  private String code;
  private String name;
  private List<FacetValue> facetValues;
  private String unit;

}