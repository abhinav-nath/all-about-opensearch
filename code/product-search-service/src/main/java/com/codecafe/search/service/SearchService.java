package com.codecafe.search.service;

import com.codecafe.search.model.FacetData;
import com.codecafe.search.model.SearchResponse;
import com.codecafe.search.model.SearchResult;
import com.codecafe.search.repository.SearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);

    private final SearchRepository searchRepository;
    private Map<String, FacetData> facetMap;

    public SearchService(SearchRepository searchRepository, Map<String, FacetData> facetMap) {
        this.searchRepository = searchRepository;
        this.facetMap = facetMap;
    }

    public SearchResponse performTextSearch(String query, int page, int size) {
        try {
            final SearchResult searchResult = searchRepository.searchProducts(query, page, size);

            if (searchResult != null && searchResult.getTotalResults() > 0) {
                LOGGER.debug("Total search results returned: {}", searchResult.getTotalResults());
                return searchResult.toDto(facetMap);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new SearchResponse();
    }

    public List<String> getSuggestions(String query) {
        return searchRepository.suggestKeywords(query);
    }

}