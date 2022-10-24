package com.codecafe.search.helper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.codecafe.search.config.FacetsConfiguration;
import com.codecafe.search.model.FacetData;
import com.codecafe.search.mustache.model.Aggregation;
import com.codecafe.search.mustache.model.Filter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
@RequiredArgsConstructor
public class FacetsBuilder {

  private final FacetsConfiguration facetsConfiguration;
  private final ObjectMapper objectMapper;

  public List<Map> buildFacets(List<FacetData> filters) {
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

  private Aggregation buildFacet(String code, List<FacetData> filters) {
    List<Filter> aggregationFilters = Optional.ofNullable(filters)
                                              .orElseGet(Collections::emptyList)
                                              .stream()
                                              .filter(filter -> !filter.getCode().equals(code))
                                              .map(filter -> new Filter(filter.getCode(),
                                                filter.getValues(),
                                                false))
                                              .collect(toList());
    if (isEmpty(aggregationFilters)) {
      return new Aggregation(code, false, false);
    } else {
      aggregationFilters.get(aggregationFilters.size() - 1).setLast(true);
      return new Aggregation(code, false, true, aggregationFilters);
    }
  }

  public List<Map> buildFilters(List<FacetData> filters) {
    if (!isEmpty(filters)) {
      List<Filter> aggregationFilters = filters.stream()
                                               .map(filter -> new Filter(filter.getCode(),
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
