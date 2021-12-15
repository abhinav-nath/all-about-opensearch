package com.codecafe.search.service;

import com.codecafe.search.model.SearchResponse;
import com.codecafe.search.model.SearchResult;
import com.codecafe.search.repository.SearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);

    private final SearchRepository searchRepository;

    public SearchService(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    public SearchResponse performTextSearch(String query, int from, int size, String sortBy, String facets) {
        try {
            final SearchResult searchResult = searchRepository.searchProducts(query, from, size, sortBy, facets);

            if (searchResult != null && searchResult.getTotalResults() > 0) {
                LOGGER.debug("Total search results returned: {}", searchResult.getTotalResults());
                return searchResult.toDto();
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