package com.codecafe.search.helper;

import com.codecafe.search.config.FacetsConfig;
import com.codecafe.search.model.FacetData;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.index.query.*;
import org.opensearch.search.aggregations.AggregationBuilders;
import org.opensearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.sort.FieldSortBuilder;
import org.opensearch.search.sort.SortBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.opensearch.index.query.QueryBuilders.boolQuery;
import static org.opensearch.search.sort.SortBuilders.fieldSort;
import static org.opensearch.search.sort.SortBuilders.scoreSort;
import static org.opensearch.search.sort.SortOrder.DESC;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
public class SearchRequestBuilder {

    private static final String AGGREGATION_FIELD = "%s.raw";

    @Value("${app.opensearch.index-name}")
    private String indexName;

    @Value("${app.search.facets-size:100}")
    private int facetsSize;

    private final FacetsConfig facetsConfig;

    public SearchRequestBuilder(FacetsConfig facetsConfig) {
        this.facetsConfig = facetsConfig;
    }

    public SearchRequest buildTextSearchRequest(String query, List<FacetData> facets, int page, int size) {
        QueryBuilder queryBuilder = buildBasicTextSearchQuery(query);

        if (!isEmpty(facets)) {
            queryBuilder = buildFilterQuery(facets, queryBuilder);
        }

        SearchRequest searchRequest = buildSearchRequestFrom(queryBuilder, page, size);
        addSorting(searchRequest);
        return searchRequest;
    }

    private BoolQueryBuilder buildBasicTextSearchQuery(String query) {
        return boolQuery()
                .should(new MatchQueryBuilder("name", query).boost(10.0f))
                .should(new MatchQueryBuilder("description", query).boost(1.0f))
                .should(new FuzzyQueryBuilder("name", query).boost(10.0f))
                .should(new WildcardQueryBuilder("name", "*" + query + "*").boost(10.0f))
                .should(new MatchPhrasePrefixQueryBuilder("name", query).boost(10.0f));
    }

    private BoolQueryBuilder buildFilterQuery(List<FacetData> facets, QueryBuilder queryBuilder) {
        BoolQueryBuilder filterQuery = new BoolQueryBuilder();

        for (FacetData facet : facets) {
            BoolQueryBuilder filterValuesQuery = new BoolQueryBuilder();
            for (String filter : facet.getValues()) {
                filterValuesQuery = filterValuesQuery.should(new MatchQueryBuilder(String.format(AGGREGATION_FIELD, facet.getCode()), filter));
            }
            filterQuery = filterQuery.must(filterValuesQuery);
        }

        return filterQuery.must(queryBuilder);
    }

    private SearchRequest buildSearchRequestFrom(QueryBuilder queryBuilder, int page, int size) {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.version(true);

        sourceBuilder.query(queryBuilder);

        sourceBuilder.from((page - 1) * size);
        sourceBuilder.size(size);

        buildAggregations().forEach(sourceBuilder::aggregation);

        searchRequest.source(sourceBuilder);
        return searchRequest;
    }

    private List<TermsAggregationBuilder> buildAggregations() {
        return facetsConfig.getFacets()
                .keySet()
                .stream()
                .map(field -> AggregationBuilders.terms(field).field(String.format(AGGREGATION_FIELD, field)).size(facetsSize))
                .collect(toList());
    }

    private void addSorting(SearchRequest searchRequest) {
        FieldSortBuilder modifiedAtSortBuilder = fieldSort("modifiedAt").order(DESC);
        FieldSortBuilder createdAtSortBuilder = fieldSort("createdAt").order(DESC);
        List<SortBuilder<? extends SortBuilder<?>>> sortOrders = List.of(scoreSort(), modifiedAtSortBuilder, createdAtSortBuilder);
        sortOrders.forEach(searchRequest.source()::sort);
    }

}
