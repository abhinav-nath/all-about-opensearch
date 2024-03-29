package com.codecafe.search.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codecafe.search.model.FacetData;
import com.codecafe.search.model.SearchResponse;
import com.codecafe.search.service.SearchService;

@Validated
@RestController
@RequestMapping(value = "/v1/search")
public class SearchController {

  private final SearchService searchService;

  public SearchController(SearchService searchService) {
    this.searchService = searchService;
  }

  @GetMapping
  public ResponseEntity<SearchResponse> textSearch(@RequestParam("query") final String query,
                                                   @RequestParam(value = "facets", required = false) final List<FacetData> facets,
                                                   @RequestParam(value = "page", defaultValue = "1") final int page,
                                                   @RequestParam(value = "size", defaultValue = "5") final int size) {

    SearchResponse searchResponse = searchService.performTextSearch(query, facets, page, size);

    return ResponseEntity.ok(searchResponse);
  }

  @GetMapping("/suggestions")
  public ResponseEntity<List<String>> autoSuggest(@RequestParam("query") final String query) {
    return ResponseEntity.ok(searchService.getSuggestions(query));
  }

}
