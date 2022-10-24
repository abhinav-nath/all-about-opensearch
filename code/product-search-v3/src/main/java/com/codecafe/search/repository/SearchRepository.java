package com.codecafe.search.repository;

import java.util.List;
import java.util.Map;

import org.opensearch.action.search.SearchResponse;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.codecafe.search.helper.SearchResponseParser;
import com.codecafe.search.helper.SearchTemplateParamsBuilder;
import com.codecafe.search.model.FacetData;
import com.codecafe.search.model.SearchResult;
import com.codecafe.search.service.SearchTemplateService;

import static com.codecafe.search.helper.SearchTemplate.TEXT_SEARCH_TEMPLATE;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SearchRepository {

  private final SearchTemplateParamsBuilder searchTemplateParamsBuilder;
  private final SearchResponseParser responseParser;
  private final SearchTemplateService searchTemplateService;

  public SearchResult searchProducts(String query, List<FacetData> filters, int page, int pageSize) {
    Map<String, Object> searchTemplateParams = searchTemplateParamsBuilder.buildTextSearchParams(query, filters, page, pageSize);

    SearchResponse searchResponse = searchTemplateService.executeTemplate(TEXT_SEARCH_TEMPLATE, searchTemplateParams);

    return responseParser.parseSearchResult(searchResponse);
  }

}