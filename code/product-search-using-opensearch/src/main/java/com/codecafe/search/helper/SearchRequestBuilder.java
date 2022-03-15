package com.codecafe.search.helper;

import com.codecafe.search.config.FacetsConfig;
import com.codecafe.search.model.FacetData;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.index.query.*;
import org.opensearch.search.aggregations.AggregationBuilder;
import org.opensearch.search.aggregations.AggregationBuilders;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.sort.FieldSortBuilder;
import org.opensearch.search.sort.SortBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.opensearch.index.query.QueryBuilders.boolQuery;
import static org.opensearch.search.sort.SortBuilders.fieldSort;
import static org.opensearch.search.sort.SortBuilders.scoreSort;
import static org.opensearch.search.sort.SortOrder.DESC;

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

        SearchRequest searchRequest = buildSearchRequestFrom(queryBuilder, facets, page, size);
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

    private SearchRequest buildSearchRequestFrom(QueryBuilder queryBuilder, List<FacetData> facets, int page, int size) {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.version(true);

        sourceBuilder.query(queryBuilder);

        sourceBuilder.from((page - 1) * size);
        sourceBuilder.size(size);

        buildAggregations(facets).forEach(sourceBuilder::aggregation);

        BoolQueryBuilder postFilterQuery = QueryBuilders.boolQuery();

        for (FacetData filter : facets) {
            BoolQueryBuilder orQueryBuilder = QueryBuilders.boolQuery();

            orQueryBuilder.should(QueryBuilders.termsQuery(filter.getCode() + ".raw", filter.getValues()));

            postFilterQuery.filter(orQueryBuilder);
        }

        postFilterQuery = postFilterQuery.filter().isEmpty() ? null : postFilterQuery;

        searchRequest.source(sourceBuilder.postFilter(postFilterQuery));
        return searchRequest;
    }

    private List<AggregationBuilder> buildAggregations(List<FacetData> facets) {
        List<AggregationBuilder> aggregationBuilders = new ArrayList<>();
        for (Map.Entry<String, FacetsConfig.FacetInfo> builtInFacet : facetsConfig.getFacets().entrySet()) {
            AggregationBuilder aggregationBuilder = createPossiblyFilteredAggregation(builtInFacet.getKey(), facets);
            aggregationBuilders.add(aggregationBuilder);
        }
        return aggregationBuilders;
    }

    private AggregationBuilder createPossiblyFilteredAggregation(String facet, List<FacetData> filters) {
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms(facet).field(facet + ".raw");

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        filters.stream()
                .filter(filter -> !filter.getCode().equals(facet)) // filter out itself
                .forEach(filter -> queryBuilder.filter(QueryBuilders.termsQuery(filter.getCode() + ".raw", filter.getValues())));

        if (!queryBuilder.filter().isEmpty()) {
            aggregationBuilder = AggregationBuilders.filter(facet, queryBuilder).subAggregation(aggregationBuilder);
        }
        return aggregationBuilder;
    }


    private void addSorting(SearchRequest searchRequest) {
        FieldSortBuilder modifiedAtSortBuilder = fieldSort("modifiedAt").order(DESC);
        FieldSortBuilder createdAtSortBuilder = fieldSort("createdAt").order(DESC);
        List<SortBuilder<? extends SortBuilder<?>>> sortOrders = List.of(scoreSort(), modifiedAtSortBuilder, createdAtSortBuilder);
        sortOrders.forEach(searchRequest.source()::sort);
    }

}
