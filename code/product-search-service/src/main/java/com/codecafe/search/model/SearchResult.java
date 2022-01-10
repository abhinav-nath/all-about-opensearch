package com.codecafe.search.model;

import com.codecafe.search.config.FacetsConfig;
import com.codecafe.search.document.ProductDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {

    private long totalResults;
    private List<ProductDocument> productDocuments;
    private Aggregations aggregations;

    public SearchResponse toDto(Map<String, FacetsConfig.FacetInfo> facetsConfig) {
        SearchResponse searchResponse = new SearchResponse();
        List<Product> products = new ArrayList<>(1);
        List<Facet> facets = null;

        if (!CollectionUtils.isEmpty(productDocuments)) {
            productDocuments.forEach(p -> products.add(p.toDto()));
        }

        if (aggregations != null) {
            facets = getFacets(facetsConfig);
        }

        return searchResponse.withProducts(products).withTotalResults(totalResults).withFacets(facets);
    }

    private List<Facet> getFacets(Map<String, FacetsConfig.FacetInfo> facetsConfig) {
        List<Facet> facets = new ArrayList<>(1);
        List<Aggregation> aggregations = this.aggregations.asList();

        for (Aggregation aggregation : aggregations) {
            List<FacetValue> facetValues = new ArrayList<>(1);

            for (Terms.Bucket bucket : ((Terms) aggregation).getBuckets()) {
                FacetValue facetValue = new FacetValue().withName(bucket.getKeyAsString()).withCount(bucket.getDocCount());
                facetValues.add(facetValue);
            }

            Facet facet = new Facet().withCode(aggregation.getName())
                    .withName(facetsConfig.get(aggregation.getName()).getDisplayName())
                    .withFacetValues(facetValues);
            facets.add(facet);
        }

        return facets;
    }

}