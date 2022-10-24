package com.codecafe.search.helper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.codecafe.search.config.FacetsConfiguration;
import com.codecafe.search.model.Filter;
import com.codecafe.search.mustache.model.Aggregation;
import com.codecafe.search.mustache.model.AggregationFilter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
@RequiredArgsConstructor
public class FacetsBuilder {

  private static final String AGGREGATION_FIELD = "%s.raw";

  private final FacetsConfiguration facetsConfiguration;
  private final ObjectMapper objectMapper;

  public List<Map> buildFacets(List<Filter> filters) {
    List<Aggregation> aggregations = facetsConfiguration.getFacets()
                                                        .keySet()
                                                        .stream()
                                                        .map(code -> buildFacet(code, filters))
                                                        .collect(toList());
    if (!isEmpty(aggregations)) {
      aggregations.get(aggregations.size() - 1).setLast(true);
    }
    return objectMapper.convertValue(aggregations, new TypeReference<>() {
    });
  }

  private Aggregation buildFacet(String code, List<Filter> filters) {
    List<AggregationFilter> aggregationFilters = Optional.ofNullable(filters)
                                                         .orElseGet(Collections::emptyList)
                                                         .stream()
                                                         .filter(filter -> !filter.getCode().equals(code))
                                                         .map(filter -> new AggregationFilter(filter.getCode(),
                                                           filter.getValues(),
                                                           false))
                                                         .collect(toList());
    if (isEmpty(aggregationFilters)) {
      return new Aggregation(code, false, false);
    } else {
      aggregationFilters.get(aggregationFilters.size() - 1).setLast(true);
      return new Aggregation(code, code, false, true, aggregationFilters);
    }
  }

  public List<Map> buildFilters(List<Filter> selectedFilters) {
    if (!isEmpty(selectedFilters)) {
      List<AggregationFilter> aggregationFilters = selectedFilters.stream()
                                                                  .map(filter -> new AggregationFilter(filter.getCode(),
                                                                    filter.getValues(),
                                                                    false))
                                                                  .collect(toList());
      aggregationFilters.get(aggregationFilters.size() - 1).setLast(true);
      return objectMapper.convertValue(aggregationFilters, new TypeReference<>() {
      });
    }
    return emptyList();
  }

}
