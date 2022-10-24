package com.codecafe.search.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.codecafe.search.model.FacetData;
import com.codecafe.search.model.TextSearchResponse;
import com.codecafe.search.service.SearchService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/search")
public class SearchController {

  private final SearchService searchService;

  @GetMapping
  public ResponseEntity<TextSearchResponse> textSearch(@RequestParam("query") final String query,
                                                       @RequestParam(value = "filters", required = false) final List<FacetData> filters,
                                                       @RequestParam(value = "page", defaultValue = "1") final int page,
                                                       @RequestParam(value = "pageSize", defaultValue = "5") final int pageSize) {

    TextSearchResponse textSearchResponse = searchService.performTextSearch(query, filters, page, pageSize);

    return ResponseEntity.ok(textSearchResponse);
  }

}