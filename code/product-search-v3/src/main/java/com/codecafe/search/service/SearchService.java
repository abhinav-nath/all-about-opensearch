package com.codecafe.search.service;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.codecafe.search.entity.SearchFilter;
import com.codecafe.search.model.FacetData;
import com.codecafe.search.model.SearchResult;
import com.codecafe.search.model.TextSearchResponse;
import com.codecafe.search.redis.repository.SearchFilterRepository;
import com.codecafe.search.repository.SearchRepository;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

  private final SearchFilterRepository searchFilterRepository;
  private final SearchRepository searchRepository;
  private final ModelMapper modelMapper;

  public TextSearchResponse performTextSearch(String query, List<FacetData> filters, int page, int pageSize) {
    SearchFilter searchFilter = SearchFilter.builder()
                                            .searchFilter("hello")
                                            .searchField("world")
                                            .build();
    SearchFilter savedSearchFilter = searchFilterRepository.save(searchFilter);

    if (nonNull(savedSearchFilter)) {
      log.info("SearchFilter saved successfully!");

      Optional<SearchFilter> retrievedSearchFilter = searchFilterRepository.findById("hello");
      retrievedSearchFilter.ifPresent(f -> log.info("Successfully retrieved SearchFilter {}:{}", f.getSearchFilter(), f.getSearchField()));
    }

    SearchResult searchResult = searchRepository.searchProducts(query, filters, page, pageSize);
    log.info("Total search results returned: {}", searchResult.getTotalResults());
    return searchResult.toSearchResponse(modelMapper);
  }

}