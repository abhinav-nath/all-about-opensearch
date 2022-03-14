package com.codecafe.search.service;

import com.codecafe.search.model.FacetData;
import com.codecafe.search.model.SearchResponse;
import com.codecafe.search.model.SearchResult;
import com.codecafe.search.repository.SearchRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SearchService {

    private final SearchRepository searchRepository;
    private final ModelMapper modelMapper;

    public SearchService(SearchRepository searchRepository, ModelMapper modelMapper) {
        this.searchRepository = searchRepository;
        this.modelMapper = modelMapper;
    }

    public SearchResponse performTextSearch(String query, List<FacetData> facets, int page, int size) {
        SearchResult searchResult = searchRepository.searchProducts(query, facets, page, size);

        log.info("Total search results returned: {}", searchResult.getTotalResults());

        return searchResult.toSearchResponse(modelMapper);
    }

}