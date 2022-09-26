package com.codecafe.search.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.codecafe.search.model.FacetData;
import com.codecafe.search.model.PopularSearchResponse;
import com.codecafe.search.model.SearchResponse;
import com.codecafe.search.model.SearchResult;
import com.codecafe.search.repository.SearchRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

  private final SearchRepository searchRepository;
  private final ModelMapper modelMapper;

  public SearchResponse performTextSearch(String query, List<FacetData> facets, String unitSystem, int page, int size) {
    searchRepository.saveSearchQuery(query);
    SearchResult searchResult = searchRepository.searchProducts(query, facets, unitSystem, page, size);
    log.info("Total search results returned: {}", searchResult.getTotalResults());
    return searchResult.toSearchResponse(modelMapper);
  }

  public PopularSearchResponse getPopularSearchQueries(int top) {
    return searchRepository.getPopularSearchQueries(top);
  }

}