package com.codecafe.search.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.codecafe.search.model.FacetData;
import com.codecafe.search.model.SearchResult;
import com.codecafe.search.model.TextSearchResponse;
import com.codecafe.search.repository.SearchRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

  private final SearchRepository searchRepository;
  private final ModelMapper modelMapper;

  public TextSearchResponse performTextSearch(String query, List<FacetData> filters, int page, int pageSize) {
    SearchResult searchResult = searchRepository.searchProducts(query, filters, page, pageSize);
    log.info("Total search results returned: {}", searchResult.getTotalResults());
    return searchResult.toSearchResponse(modelMapper);
  }

}