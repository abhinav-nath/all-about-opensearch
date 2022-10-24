package com.codecafe.search.mustache.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Aggregation {

  private String code;
  private String field;
  private boolean last;
  private boolean hasSubAggregation;
  private List<Filter> aggregationFilters;

  public Aggregation(String code, boolean last, boolean hasSubAggregation) {
    this.code = code;
    this.field = code;
    this.last = last;
    this.hasSubAggregation = hasSubAggregation;
  }

  public Aggregation(String code, boolean last, boolean hasSubAggregation, List<Filter> aggregationFilters) {
    this.code = code;
    this.field = code;
    this.last = last;
    this.hasSubAggregation = hasSubAggregation;
    this.aggregationFilters = aggregationFilters;
  }

}