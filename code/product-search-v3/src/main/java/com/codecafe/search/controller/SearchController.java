package com.codecafe.search.controller;

import com.codecafe.search.model.AutoSuggestResponse;
import com.codecafe.search.model.FacetData;
import com.codecafe.search.model.TextSearchResponse;
import com.codecafe.search.service.SearchService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;

import java.util.List;

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
                                                       @Min(1) @RequestParam(value = "page", defaultValue = "1") final int page,
                                                       @Min(1) @RequestParam(value = "pageSize", defaultValue = "5") final int pageSize) {

    TextSearchResponse textSearchResponse = searchService.performTextSearch(query, facets, page, pageSize);

    return ResponseEntity.ok(textSearchResponse);
  }

}
