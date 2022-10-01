package com.codecafe.search.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.codecafe.search.model.AutoSuggestResponse;
import com.codecafe.search.model.FacetData;
import com.codecafe.search.model.TextSearchResponse;
import com.codecafe.search.service.SearchService;

import static org.apache.commons.lang3.StringUtils.normalizeSpace;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/search")
public class SearchController {

  private final SearchService searchService;

  @GetMapping(value = "/suggestions")
  public ResponseEntity<AutoSuggestResponse> getSuggestions(@RequestParam(value = "query") String query) {
    AutoSuggestResponse autoSuggestResponse = searchService.getAutocompleteSuggestions(normalizeSpace(query));

    return ResponseEntity.ok(autoSuggestResponse);
  }

  @GetMapping
  public ResponseEntity<TextSearchResponse> textSearch(@RequestParam("query") final String query,
                                                       @RequestParam(value = "facets", required = false) final List<FacetData> facets,
                                                       @RequestParam(value = "page", defaultValue = "1") final int page,
                                                       @RequestParam(value = "size", defaultValue = "5") final int size) {

    TextSearchResponse textSearchResponse = searchService.performTextSearch(query, facets, page, size);

    return ResponseEntity.ok(textSearchResponse);
  }

}
