package com.codecafe.search.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.codecafe.search.model.FacetData;

import static com.codecafe.search.utils.Constants.PARAM_AGGREGATIONS;
import static com.codecafe.search.utils.Constants.PARAM_FACETS_SIZE;
import static com.codecafe.search.utils.Constants.PARAM_FILTERS;
import static com.codecafe.search.utils.Constants.PARAM_FROM;
import static com.codecafe.search.utils.Constants.PARAM_FUZZINESS_VALUE;
import static com.codecafe.search.utils.Constants.PARAM_QUERY_STRING;
import static com.codecafe.search.utils.Constants.PARAM_SIZE;

@Component
public class SearchTemplateParamsBuilder {

  private final FacetsBuilder facetsBuilder;

  @Value("${app.search.facets-size}")
  private int facetsSize;

  @Value("#{${app.search.query-boost-fields}}")
  private Map<String, Float> queryBoostFields;

  @Value("${app.search.fuzzinessValue}")
  private String fuzzinessValue;

  public SearchTemplateParamsBuilder(FacetsBuilder facetsBuilder) {
    this.facetsBuilder = facetsBuilder;
  }

  public Map<String, Object> buildTextSearchParams(String query, List<FacetData> filters, int page, int pageSize) {
    Map<String, Object> params = new HashMap<>(queryBoostFields);

    params.put(PARAM_QUERY_STRING, query);
    addPaginationParams(page, pageSize, params);
    addAggregationParams(filters, params);

    return params;
  }

  private void addAggregationParams(List<FacetData> filters, Map<String, Object> params) {
    params.put(PARAM_AGGREGATIONS, facetsBuilder.buildFacets(filters));
    params.put(PARAM_FILTERS, facetsBuilder.buildFilters(filters));
    params.put(PARAM_FACETS_SIZE, facetsSize);
    params.put(PARAM_FUZZINESS_VALUE, fuzzinessValue);
  }

  private void addPaginationParams(int page, int size, Map<String, Object> params) {
    params.put(PARAM_FROM, (page - 1) * size);
    params.put(PARAM_SIZE, size);
  }

}