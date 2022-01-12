package com.codecafe.search.repository;

import com.codecafe.search.config.FacetsConfig;
import com.codecafe.search.document.ProductDocument;
import com.codecafe.search.model.FacetData;
import com.codecafe.search.model.SearchResult;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.search.sort.SortBuilders.fieldSort;
import static org.elasticsearch.search.sort.SortBuilders.scoreSort;
import static org.elasticsearch.search.sort.SortOrder.DESC;
import static org.springframework.data.elasticsearch.core.mapping.IndexCoordinates.of;
import static org.springframework.util.CollectionUtils.isEmpty;

@Repository
public class SearchRepository {

    @Value("${app.search.index-name}")
    private String indexName;

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final FacetsConfig facetsConfig;

    @Autowired
    public SearchRepository(ElasticsearchRestTemplate elasticsearchTemplate, FacetsConfig facetsConfig) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.facetsConfig = facetsConfig;
    }

    public SearchResult searchProducts(String query, List<FacetData> facets, int page, int size) {
        List<ProductDocument> matchedProducts = new ArrayList<>(1);
        SearchResult searchResult = new SearchResult();

        String wildcardQuery = "*" + query + "*";

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();

        searchQueryBuilder = addSorting(searchQueryBuilder);

        if (!isEmpty(facets))
            searchQueryBuilder = searchQueryBuilder.withQuery(buildFilterQuery(facets).must(buildShouldClauses(query, wildcardQuery)));
        else
            searchQueryBuilder = searchQueryBuilder.withQuery(buildShouldClauses(query, wildcardQuery));

        searchQueryBuilder = buildAggregations(searchQueryBuilder);

        NativeSearchQuery searchQuery = searchQueryBuilder.withPageable(PageRequest.of(page - 1, size)).build();

        SearchHits<ProductDocument> searchHits = elasticsearchTemplate.search(searchQuery, ProductDocument.class, of(indexName));

        if (!isEmpty(searchHits.getSearchHits())) {
            searchHits.getSearchHits()
                    .forEach(hit -> matchedProducts.add(hit.getContent()));

            searchResult = SearchResult.builder()
                    .productDocuments(matchedProducts)
                    .totalResults(searchHits.getTotalHits())
                    .aggregations(searchHits.getAggregations())
                    .build();
        }

        return searchResult;
    }

    private NativeSearchQueryBuilder addSorting(NativeSearchQueryBuilder searchQueryBuilder) {
        return searchQueryBuilder
                .withSort(scoreSort())
                .withSort(fieldSort("dateModified").order(DESC))
                .withSort(fieldSort("dateAdded").order(DESC));
    }

    private BoolQueryBuilder buildFilterQuery(List<FacetData> facets) {
        BoolQueryBuilder filterQuery = new BoolQueryBuilder();

        for (FacetData facet : facets) {
            BoolQueryBuilder filterValuesQuery = new BoolQueryBuilder();
            for (String filter : facet.getValues()) {
                filterValuesQuery = filterValuesQuery.should(new MatchQueryBuilder(facet.getCode(), filter));
            }
            filterQuery = filterQuery.must(filterValuesQuery);
        }

        return filterQuery;
    }

    private BoolQueryBuilder buildShouldClauses(String query, String wildcardQuery) {
        return boolQuery()
                .should(new MatchQueryBuilder("name", query).boost(10.0f))
                .should(new MatchQueryBuilder("description", query).boost(1.0f))
                .should(new FuzzyQueryBuilder("name", query).boost(10.0f))
                .should(new WildcardQueryBuilder("name", wildcardQuery).boost(10.0f))
                .should(new MatchPhrasePrefixQueryBuilder("name", query).boost(10.0f));
    }

    public NativeSearchQueryBuilder buildAggregations(NativeSearchQueryBuilder searchQueryBuilder) {

        for (Map.Entry<String, FacetsConfig.FacetInfo> entry : facetsConfig.getFacets().entrySet()) {
            searchQueryBuilder.addAggregation(AggregationBuilders.terms(entry.getKey()).field(entry.getKey() + ".raw"));
        }

        return searchQueryBuilder;
    }

    public List<String> suggestKeywords(String query) {
        SuggestionBuilder suggestionBuilder = SuggestBuilders.completionSuggestion("suggest").prefix(query).skipDuplicates(true);

        SearchResponse suggestResponse = elasticsearchTemplate.suggest(
                new SuggestBuilder().addSuggestion("auto-suggestions", suggestionBuilder), of(indexName));

        return extractKeywordSuggestionsFrom(query, suggestResponse);
    }

    private List<String> extractKeywordSuggestionsFrom(String query, SearchResponse suggestResponse) {
        Set<String> keywords = new LinkedHashSet<>();
        if (suggestResponse.getSuggest() != null) {
            CompletionSuggestion completionSuggestion = suggestResponse.getSuggest().getSuggestion("auto-suggestions");

            if (completionSuggestion != null && !isEmpty(completionSuggestion.getEntries())) {
                completionSuggestion.getEntries().forEach(e -> {
                    if (isEmpty(e.getOptions()))
                        return;

                    e.getOptions().forEach(o -> {
                        ((HashMap<String, List<String>>) o.getHit().getSourceAsMap().get("suggest")).get("input")
                                .forEach(term -> {
                                    if (term.startsWith(query))
                                        keywords.add(term);
                                });
                    });
                });
            }
        }

        return new ArrayList<>(keywords);
    }

}