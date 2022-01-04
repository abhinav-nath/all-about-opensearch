package com.codecafe.search.repository;

import com.codecafe.search.document.ProductDocument;
import com.codecafe.search.model.SearchResult;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
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

    private ElasticsearchRestTemplate elasticsearchTemplate;

    @Autowired
    public SearchRepository(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    public SearchResult searchProducts(String query, int page, int size) {

        List<ProductDocument> matchedProducts = new ArrayList<>(1);
        SearchResult searchResult = new SearchResult();

        String wildcardQuery = "*" + query + "*";

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withSort(scoreSort())
                .withSort(fieldSort("dateModified").order(DESC))
                .withSort(fieldSort("dateAdded").order(DESC))
                .withQuery(boolQuery()
                        .should(new MatchQueryBuilder("name", query).boost(10.0f))
                        .should(new MatchQueryBuilder("description", query).boost(1.0f))
                        .should(new FuzzyQueryBuilder("name", query).boost(10.0f))
                        .should(new WildcardQueryBuilder("name", wildcardQuery).boost(10.0f))
                        .should(new MatchPhrasePrefixQueryBuilder("name", query).boost(10.0f)))
                .addAggregation(AggregationBuilders.terms("categories").field("categories.raw"))
                .addAggregation(AggregationBuilders.terms("brand").field("brand.raw"))
                .addAggregation(AggregationBuilders.terms("color").field("generalAttributes.colorFamily.raw"))
                .withPageable(PageRequest.of(page - 1, size))
                .build();

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