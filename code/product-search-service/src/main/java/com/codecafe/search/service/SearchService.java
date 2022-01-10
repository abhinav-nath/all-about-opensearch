package com.codecafe.search.service;

import com.codecafe.search.config.Config;
import com.codecafe.search.config.FacetsConfig;
import com.codecafe.search.model.FacetData;
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
    private final FacetsConfig facetsConfig;

    public SearchService(SearchRepository searchRepository, FacetsConfig facetsConfig) {
        this.searchRepository = searchRepository;
        this.facetsConfig = facetsConfig;
    }

    public SearchResponse performTextSearch(String query, List<FacetData> facets, int page, int size) {
        try {
            final SearchResult searchResult = searchRepository.searchProducts(query, facets, page, size);

            if (searchResult != null && searchResult.getTotalResults() > 0) {
                LOGGER.debug("Total search results returned: {}", searchResult.getTotalResults());
                return searchResult.toDto(facetsConfig.getFacets());
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